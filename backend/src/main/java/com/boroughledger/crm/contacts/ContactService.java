package com.boroughledger.crm.contacts;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.users.AppUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class ContactService {
 private final ContactRepository repository; private final TenantGuard guard; private final AuditService audit; private final com.boroughledger.crm.users.UserRepository users;

 public ContactService(ContactRepository repository,TenantGuard guard,AuditService audit,com.boroughledger.crm.users.UserRepository users){this.repository=repository;this.guard=guard;this.audit=audit;this.users=users;}
 @Transactional(readOnly=true) public Page<ContactDto.Response> list(AppUser actor,int page,int size,String search){
  return repository.search(actor.getOrganizationId(),search==null||search.isBlank()?null:search.strip(),Checks.page(page,size)).map(ContactDto.Response::from);
 }
 @Transactional(readOnly=true) public ContactDto.Response get(AppUser actor,UUID id){return ContactDto.Response.from(find(actor,id));}
 @Transactional public ContactDto.Response create(AppUser actor,ContactDto.Request r){
  validate(actor,r); Contact obj=repository.save(new Contact(actor.getOrganizationId(),r)); audit.log(actor,"CREATED","CONTACT",obj.getId());return ContactDto.Response.from(obj);
 }
 @Transactional public ContactDto.Response update(AppUser actor,UUID id,ContactDto.Request r){
  validate(actor,r);Contact obj=find(actor,id);obj.update(r);audit.log(actor,"UPDATED","CONTACT",id);return ContactDto.Response.from(obj);
 }
 @Transactional public void archive(AppUser actor,UUID id){Contact obj=find(actor,id);obj.archive();audit.log(actor,"ARCHIVED","CONTACT",id);}
 private Contact find(AppUser a,UUID id){return repository.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));}
 private void validate(AppUser actor,ContactDto.Request r){
  guard.active("Customer",r.customerId(),actor.getOrganizationId());
  if(r.ownerId()!=null && !users.existsByIdAndOrganizationIdAndEnabledTrue(r.ownerId(),actor.getOrganizationId())) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Responsable inválido");

 }
}
