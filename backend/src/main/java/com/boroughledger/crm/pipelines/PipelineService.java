package com.boroughledger.crm.pipelines;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.users.AppUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class PipelineService {
 private final PipelineRepository repository; private final TenantGuard guard; private final AuditService audit; private final PipelineStageRepository stages; 
 public PipelineService(PipelineRepository repository,TenantGuard guard,AuditService audit,PipelineStageRepository stages){this.repository=repository;this.guard=guard;this.audit=audit;this.stages=stages;}
 @Transactional(readOnly=true) public Page<PipelineDto.Response> list(AppUser actor,int page,int size,String search){
  return repository.search(actor.getOrganizationId(),search==null||search.isBlank()?null:search.strip(),Checks.page(page,size)).map(PipelineDto.Response::from);
 }
 @Transactional(readOnly=true) public PipelineDto.Response get(AppUser actor,UUID id){return PipelineDto.Response.from(find(actor,id));}
 @Transactional public PipelineDto.Response create(AppUser actor,PipelineDto.Request r){
  validate(actor,r); Pipeline obj=repository.save(new Pipeline(actor.getOrganizationId(),r));
  String[] labels={"Prospecto calificado","Contacto establecido","Propuesta enviada","Negociación","Ganada","Perdida"};
  for(int i=0;i<labels.length;i++) stages.save(new PipelineStage(actor.getOrganizationId(),obj.getId(),labels[i],i,i==4?"GANADA":i==5?"PERDIDA":"ABIERTA"));
  audit.log(actor,"CREATED","PIPELINE",obj.getId());return PipelineDto.Response.from(obj);
 }
 @Transactional public PipelineDto.Response update(AppUser actor,UUID id,PipelineDto.Request r){
  validate(actor,r);Pipeline obj=find(actor,id);obj.update(r);audit.log(actor,"UPDATED","PIPELINE",id);return PipelineDto.Response.from(obj);
 }
 @Transactional public void archive(AppUser actor,UUID id){Pipeline obj=find(actor,id);obj.archive();audit.log(actor,"ARCHIVED","PIPELINE",id);}
 private Pipeline find(AppUser a,UUID id){return repository.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));}
 private void validate(AppUser actor,PipelineDto.Request r){

 }
}
