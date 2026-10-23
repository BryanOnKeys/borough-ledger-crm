package com.boroughledger.crm.opportunities;
import com.boroughledger.crm.pipelines.*;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.users.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class OpportunityService {
 private final OpportunityRepository opportunities; private final PipelineStageRepository stages; private final OpportunityStageHistoryRepository history;
 private final UserRepository users; private final TenantGuard guard; private final AuditService audit; private final com.boroughledger.crm.contacts.ContactRepository contacts;
 public OpportunityService(OpportunityRepository opportunities,PipelineStageRepository stages,OpportunityStageHistoryRepository history,UserRepository users,TenantGuard guard,AuditService audit,com.boroughledger.crm.contacts.ContactRepository contacts){this.opportunities=opportunities;this.stages=stages;this.history=history;this.users=users;this.guard=guard;this.audit=audit;this.contacts=contacts;}
 public record Input(@NotBlank @Size(max=180) String title,@NotNull UUID customerId,UUID contactId,UUID ownerId,@NotNull UUID pipelineId,@NotNull UUID stageId,@NotNull @DecimalMin("0.00") BigDecimal amount,@NotBlank @Pattern(regexp="[A-Z]{3}") String currency,LocalDate expectedClose,@Size(max=2000) String description){}
 public record Edit(@NotBlank @Size(max=180) String title, UUID contactId,UUID ownerId,@NotNull @DecimalMin("0.00") BigDecimal amount,@NotBlank @Pattern(regexp="[A-Z]{3}") String currency,LocalDate expectedClose,@Size(max=2000) String description){}
 public record Move(@NotNull UUID stageId,@Size(max=500) String lossReason){}
 public record View(UUID id,String title,UUID customerId,UUID contactId,UUID ownerId,UUID pipelineId,UUID stageId,BigDecimal amount,String currency,String status,LocalDate expectedClose,LocalDate closedAt,String lossReason,String description,long version){static View from(Opportunity o){return new View(o.getId(),o.getTitle(),o.getCustomerId(),o.getContactId(),o.getOwnerId(),o.getPipelineId(),o.getStageId(),o.getAmount(),o.getCurrency(),o.getStatus(),o.getExpectedClose(),o.getClosedAt(),o.getLossReason(),o.getDescription(),o.getVersion());}}
 @Transactional(readOnly=true) public Page<View> list(AppUser actor,int page,int size){return opportunities.findByOrganizationIdAndArchivedAtIsNull(actor.getOrganizationId(),Checks.page(page,size)).map(View::from);}
 @Transactional(readOnly=true) public View get(AppUser actor,UUID id){return View.from(find(actor,id));}
 @Transactional public View create(AppUser a,Input r){
  guard.active("Customer",r.customerId(),a.getOrganizationId()); validContact(a,r.customerId(),r.contactId()); guard.active("Pipeline",r.pipelineId(),a.getOrganizationId());checkOwner(a,r.ownerId());
  PipelineStage stage=stage(a,r.pipelineId(),r.stageId());Checks.require("ABIERTA".equals(stage.getOutcome()),"Una oportunidad nueva debe iniciar en una etapa abierta");
  Opportunity o=opportunities.save(new Opportunity(a.getOrganizationId(),r.title(),r.customerId(),r.contactId(),r.ownerId(),r.pipelineId(),r.stageId(),r.amount(),r.currency(),r.expectedClose(),r.description(),"ABIERTA"));history.save(new OpportunityStageHistory(a.getOrganizationId(),o.getId(),null,o.getStageId(),a.getId()));audit.log(a,"CREATED","OPPORTUNITY",o.getId());return View.from(o);
 }
 @Transactional public View update(AppUser a,UUID id,Edit r){Opportunity o=find(a,id);Checks.require("ABIERTA".equals(o.getStatus()),"Una oportunidad cerrada no se modifica");validContact(a,o.getCustomerId(),r.contactId());checkOwner(a,r.ownerId());o.update(r.title(),r.contactId(),r.ownerId(),r.amount(),r.currency(),r.expectedClose(),r.description());audit.log(a,"UPDATED","OPPORTUNITY",id);return View.from(o);}
 @Transactional public View move(AppUser a,UUID id,Move r){Opportunity o=find(a,id);Checks.require("ABIERTA".equals(o.getStatus()),"Una oportunidad cerrada no puede cambiar de etapa");PipelineStage next=stage(a,o.getPipelineId(),r.stageId());Checks.require(!next.getId().equals(o.getStageId()),"La oportunidad ya está en esa etapa");if("PERDIDA".equals(next.getOutcome())) Checks.require(r.lossReason()!=null&&!r.lossReason().isBlank(),"Indica el motivo de pérdida");UUID prev=o.getStageId();o.move(next.getId(),next.getOutcome(),r.lossReason());history.save(new OpportunityStageHistory(a.getOrganizationId(),o.getId(),prev,next.getId(),a.getId()));audit.log(a,"MOVED","OPPORTUNITY",id);return View.from(o);}
 @Transactional(readOnly=true) public List<OpportunityStageHistory> history(AppUser a,UUID id){find(a,id);return history.findByOrganizationIdAndOpportunityIdOrderByChangedAtDesc(a.getOrganizationId(),id);}
 @Transactional public void archive(AppUser a,UUID id){Opportunity o=find(a,id);o.archive();audit.log(a,"ARCHIVED","OPPORTUNITY",id);}
 private Opportunity find(AppUser a,UUID id){return opportunities.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Oportunidad no encontrada"));}
 private PipelineStage stage(AppUser a,UUID pipeline,UUID id){return stages.findByIdAndOrganizationIdAndArchivedAtIsNull(id,a.getOrganizationId()).filter(s->s.getPipelineId().equals(pipeline)).orElseThrow(()->new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Etapa no pertenece al embudo"));}
 private void validContact(AppUser a,UUID customerId,UUID contactId){
  if(contactId==null)return;
  var c=contacts.findByIdAndOrganizationIdAndArchivedAtIsNull(contactId,a.getOrganizationId()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Contacto no encontrado"));
  Checks.require(c.getCustomerId()==null||c.getCustomerId().equals(customerId),"El contacto está vinculado a otra empresa");
 }
 private void checkOwner(AppUser a,UUID id){if(id!=null&&!users.existsByIdAndOrganizationIdAndEnabledTrue(id,a.getOrganizationId()))throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Responsable inválido");}
}
