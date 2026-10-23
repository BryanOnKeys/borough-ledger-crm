package com.boroughledger.crm.customers;
import com.boroughledger.crm.users.*;
import com.boroughledger.crm.shared.*;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/customers")
public class CustomerDetailsController {
 private final CustomerRepository customers;private final UserRepository users;private final AuditService audit;
 public CustomerDetailsController(CustomerRepository customers,UserRepository users,AuditService audit){this.customers=customers;this.users=users;this.audit=audit;}
 public record Details(@Size(max=180) String commercialName,@Size(max=30) String taxId,@Size(max=100) String country,@Size(max=300) String address,@Size(max=220) String website,@Size(max=2000) String description, UUID ownerId){}
 @GetMapping("/{id}/details") @Transactional(readOnly=true) public Details get(@AuthenticationPrincipal AppUser a,@PathVariable UUID id){Customer c=find(a,id);return from(c);}
 @PatchMapping("/{id}/details") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')") @Transactional public Details edit(@AuthenticationPrincipal AppUser a,@PathVariable UUID id,@Valid @RequestBody Details d){Customer c=find(a,id);if(d.ownerId()!=null&&!users.existsByIdAndOrganizationIdAndEnabledTrue(d.ownerId(),a.getOrganizationId()))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Responsable inválido");c.details(d.commercialName(),d.taxId(),d.country(),d.address(),d.website(),d.description(),d.ownerId());audit.log(a,"UPDATED_DETAILS","CUSTOMER",id);return from(c);}
 private Customer find(AppUser a,UUID id){return customers.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));}
 private Details from(Customer c){return new Details(c.getCommercialName(),c.getTaxId(),c.getCountry(),c.getAddress(),c.getWebsite(),c.getDescription(),c.getOwnerId());}
}
