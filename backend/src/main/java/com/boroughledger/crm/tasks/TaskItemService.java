package com.boroughledger.crm.tasks;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.users.AppUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class TaskItemService {
 private final TaskItemRepository repository; private final TenantGuard guard; private final AuditService audit; private final com.boroughledger.crm.users.UserRepository users;

 public TaskItemService(TaskItemRepository repository,TenantGuard guard,AuditService audit,com.boroughledger.crm.users.UserRepository users){this.repository=repository;this.guard=guard;this.audit=audit;this.users=users;}
 @Transactional(readOnly=true) public Page<TaskItemDto.Response> list(AppUser actor,int page,int size,String search){
  return repository.search(actor.getOrganizationId(),search==null||search.isBlank()?null:search.strip(),Checks.page(page,size)).map(TaskItemDto.Response::from);
 }
 @Transactional(readOnly=true) public TaskItemDto.Response get(AppUser actor,UUID id){return TaskItemDto.Response.from(find(actor,id));}
 @Transactional public TaskItemDto.Response create(AppUser actor,TaskItemDto.Request r){
  validate(actor,r); TaskItem obj=repository.save(new TaskItem(actor.getOrganizationId(),r)); audit.log(actor,"CREATED","TASKITEM",obj.getId());return TaskItemDto.Response.from(obj);
 }
 @Transactional public TaskItemDto.Response update(AppUser actor,UUID id,TaskItemDto.Request r){
  validate(actor,r);TaskItem obj=find(actor,id);obj.update(r);audit.log(actor,"UPDATED","TASKITEM",id);return TaskItemDto.Response.from(obj);
 }
 @Transactional public void archive(AppUser actor,UUID id){TaskItem obj=find(actor,id);obj.archive();audit.log(actor,"ARCHIVED","TASKITEM",id);}
 private TaskItem find(AppUser a,UUID id){return repository.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));}
 private void validate(AppUser actor,TaskItemDto.Request r){
  Checks.oneOf(r.status(),"PENDIENTE","EN_PROGRESO","COMPLETADA","CANCELADA");
  Checks.oneOf(r.priority(),"BAJA","MEDIA","ALTA");
  guard.active("Opportunity",r.opportunityId(),actor.getOrganizationId());
  if(r.ownerId()!=null && !users.existsByIdAndOrganizationIdAndEnabledTrue(r.ownerId(),actor.getOrganizationId())) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Responsable inválido");

 }
}
