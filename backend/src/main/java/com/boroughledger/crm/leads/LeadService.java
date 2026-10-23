package com.boroughledger.crm.leads;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.users.AppUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class LeadService {
 private final LeadRepository repository; private final TenantGuard guard; private final AuditService audit; private final com.boroughledger.crm.users.UserRepository users;

 public LeadService(LeadRepository repository,TenantGuard guard,AuditService audit,com.boroughledger.crm.users.UserRepository users){this.repository=repository;this.guard=guard;this.audit=audit;this.users=users;}
 @Transactional(readOnly=true) public Page<LeadDto.Response> list(AppUser actor,int page,int size,String search){
  return repository.search(actor.getOrganizationId(),search==null||search.isBlank()?null:search.strip(),Checks.page(page,size)).map(LeadDto.Response::from);
 }
 @Transactional(readOnly=true) public LeadDto.Response get(AppUser actor,UUID id){return LeadDto.Response.from(find(actor,id));}
 @Transactional public LeadDto.Response create(AppUser actor,LeadDto.Request r){
  validate(actor,r); Lead obj=repository.save(new Lead(actor.getOrganizationId(),r)); audit.log(actor,"CREATED","LEAD",obj.getId());return LeadDto.Response.from(obj);
 }
 @Transactional public LeadDto.Response update(AppUser actor,UUID id,LeadDto.Request r){
  validate(actor,r);Lead obj=find(actor,id);Checks.require(!"CONVERTIDO".equals(obj.getStatus()),"Un prospecto convertido no puede editarse");obj.update(r);audit.log(actor,"UPDATED","LEAD",id);return LeadDto.Response.from(obj);
 }
 @Transactional public void archive(AppUser actor,UUID id){Lead obj=find(actor,id);obj.archive();audit.log(actor,"ARCHIVED","LEAD",id);}
 private Lead find(AppUser a,UUID id){return repository.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));}
 private void validate(AppUser actor,LeadDto.Request r){
  Checks.oneOf(r.status(),"NUEVO","CONTACTADO","CALIFICADO","DESCARTADO");
  if(r.ownerId()!=null && !users.existsByIdAndOrganizationIdAndEnabledTrue(r.ownerId(),actor.getOrganizationId())) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Responsable inválido");

 }
}
