package com.boroughledger.crm.activities;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.users.AppUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class ActivityService {
 private final ActivityRepository repository; private final TenantGuard guard; private final AuditService audit; private final com.boroughledger.crm.users.UserRepository users;

 public ActivityService(ActivityRepository repository,TenantGuard guard,AuditService audit,com.boroughledger.crm.users.UserRepository users){this.repository=repository;this.guard=guard;this.audit=audit;this.users=users;}
 @Transactional(readOnly=true) public Page<ActivityDto.Response> list(AppUser actor,int page,int size,String search){
  return repository.search(actor.getOrganizationId(),search==null||search.isBlank()?null:search.strip(),Checks.page(page,size)).map(ActivityDto.Response::from);
 }
 @Transactional(readOnly=true) public ActivityDto.Response get(AppUser actor,UUID id){return ActivityDto.Response.from(find(actor,id));}
 @Transactional public ActivityDto.Response create(AppUser actor,ActivityDto.Request r){
  validate(actor,r); Activity obj=repository.save(new Activity(actor.getOrganizationId(),r)); audit.log(actor,"CREATED","ACTIVITY",obj.getId());return ActivityDto.Response.from(obj);
 }
 @Transactional public ActivityDto.Response update(AppUser actor,UUID id,ActivityDto.Request r){
  validate(actor,r);Activity obj=find(actor,id);obj.update(r);audit.log(actor,"UPDATED","ACTIVITY",id);return ActivityDto.Response.from(obj);
 }
 @Transactional public void archive(AppUser actor,UUID id){Activity obj=find(actor,id);obj.archive();audit.log(actor,"ARCHIVED","ACTIVITY",id);}
 private Activity find(AppUser a,UUID id){return repository.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));}
 private void validate(AppUser actor,ActivityDto.Request r){
  Checks.oneOf(r.kind(),"LLAMADA","REUNION","CORREO_REGISTRADO","SEGUIMIENTO","OTRA");
  guard.active("Customer",r.customerId(),actor.getOrganizationId());
  guard.active("Contact",r.contactId(),actor.getOrganizationId());
  guard.active("Opportunity",r.opportunityId(),actor.getOrganizationId());
  if(r.ownerId()!=null && !users.existsByIdAndOrganizationIdAndEnabledTrue(r.ownerId(),actor.getOrganizationId())) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Responsable inválido");

 }
}
