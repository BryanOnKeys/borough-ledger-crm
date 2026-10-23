package com.boroughledger.crm.leads;
import com.boroughledger.crm.contacts.*;
import com.boroughledger.crm.customers.*;
import com.boroughledger.crm.pipelines.*;
import com.boroughledger.crm.opportunities.*;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.users.AppUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
@Service
public class LeadConversionService {
 private final LeadRepository leads; private final CustomerRepository customers; private final ContactRepository contacts; private final OpportunityService deals; private final AuditService audit;
 public LeadConversionService(LeadRepository leads,CustomerRepository customers,ContactRepository contacts,OpportunityService deals,AuditService audit){this.leads=leads;this.customers=customers;this.contacts=contacts;this.deals=deals;this.audit=audit;}
 public record Conversion(@NotNull UUID pipelineId,@NotNull UUID stageId, UUID customerId, UUID contactId,String dealTitle,String currency){}
 public record ConversionResult(UUID customerId,UUID contactId,OpportunityService.View opportunity){}
 // Se vinculan registros existentes para evitar duplicados de clientes.
 @Transactional public ConversionResult convert(AppUser a,UUID leadId,Conversion r){
  Lead lead=leads.findByIdAndOrganizationIdAndArchivedAtIsNull(leadId,a.getOrganizationId()).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
  Checks.require("CALIFICADO".equals(lead.getStatus()),"Solo un prospecto calificado puede convertirse");
  Customer customer;
  if(r.customerId()!=null){customer=customers.findByIdAndOrganizationIdAndArchivedAtIsNull(r.customerId(),a.getOrganizationId()).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Cliente no encontrado"));}
  else {Checks.require(lead.getCompany()!=null&&!lead.getCompany().isBlank(),"El prospecto necesita empresa para convertirlo automáticamente");
   customer=customers.findFirstByOrganizationIdAndNameIgnoreCaseAndArchivedAtIsNull(a.getOrganizationId(),lead.getCompany()).orElseGet(()->customers.save(new Customer(a.getOrganizationId(),lead.getCompany(),null,null,null)));
  }
  Contact contact;
  if(r.contactId()!=null) contact=contacts.findByIdAndOrganizationIdAndArchivedAtIsNull(r.contactId(),a.getOrganizationId()).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Contacto no encontrado"));
  else {
   contact=(lead.getEmail()==null||lead.getEmail().isBlank()?java.util.Optional.<Contact>empty():contacts.findFirstByOrganizationIdAndEmailIgnoreCaseAndArchivedAtIsNull(a.getOrganizationId(),lead.getEmail())).orElse(null);
   if(contact==null){String[] part=lead.getName().trim().split("\\s+",2);contact=contacts.save(new Contact(a.getOrganizationId(),new ContactDto.Request(part[0],part.length>1?part[1]:"Sin apellido",lead.getEmail(),lead.getPhone(),null,customer.getId(),lead.getOwnerId(),"Contacto originado por conversión de prospecto")));}
  }
  Checks.require(contact.getCustomerId()==null||contact.getCustomerId().equals(customer.getId()),"Contacto y empresa incompatibles");
  OpportunityService.View result=deals.create(a,new OpportunityService.Input(r.dealTitle()==null||r.dealTitle().isBlank()?"Oportunidad de "+lead.getName():r.dealTitle(),customer.getId(),contact.getId(),lead.getOwnerId(),r.pipelineId(),r.stageId(),lead.getExpectedValue()==null?java.math.BigDecimal.ZERO:lead.getExpectedValue(),r.currency()==null?"PEN":r.currency(),null,"Prospecto convertido"));
  lead.setConverted();audit.log(a,"CONVERTED","LEAD",leadId);return new ConversionResult(customer.getId(),contact.getId(),result);
 }
}
