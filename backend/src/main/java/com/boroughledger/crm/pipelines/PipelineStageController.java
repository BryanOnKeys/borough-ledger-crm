package com.boroughledger.crm.pipelines;
import com.boroughledger.crm.shared.*;
import com.boroughledger.crm.users.AppUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/pipelines/{pipelineId}/stages")
public class PipelineStageController {
 private final PipelineStageRepository stages;private final TenantGuard guard;private final AuditService audit; private final com.boroughledger.crm.opportunities.OpportunityRepository opportunities;
 public PipelineStageController(PipelineStageRepository stages,TenantGuard guard,AuditService audit,com.boroughledger.crm.opportunities.OpportunityRepository opportunities){this.stages=stages;this.guard=guard;this.audit=audit;this.opportunities=opportunities;}
 public record StageInput(@NotBlank @Size(max=100) String name,@Min(0) int position,@NotBlank String outcome){}
 public record StageView(UUID id,UUID pipelineId,String name,int position,String outcome,long version){public static StageView from(PipelineStage s){return new StageView(s.getId(),s.getPipelineId(),s.getName(),s.getPosition(),s.getOutcome(),s.getVersion());}}
 @GetMapping @Transactional(readOnly=true) public List<StageView> list(@AuthenticationPrincipal AppUser actor,@PathVariable UUID pipelineId){guard.active("Pipeline",pipelineId,actor.getOrganizationId());return stages.findByOrganizationIdAndPipelineIdAndArchivedAtIsNullOrderByPositionAsc(actor.getOrganizationId(),pipelineId).stream().map(StageView::from).toList();}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @Transactional public StageView create(@AuthenticationPrincipal AppUser actor,@PathVariable UUID pipelineId,@Valid @RequestBody StageInput r){guard.active("Pipeline",pipelineId,actor.getOrganizationId());String outcome=Checks.oneOf(r.outcome(),"ABIERTA","GANADA","PERDIDA");PipelineStage s=stages.save(new PipelineStage(actor.getOrganizationId(),pipelineId,r.name(),r.position(),outcome));audit.log(actor,"CREATED","STAGE",s.getId());return StageView.from(s);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") @Transactional public StageView update(@AuthenticationPrincipal AppUser actor,@PathVariable UUID pipelineId,@PathVariable UUID id,@Valid @RequestBody StageInput r){PipelineStage s=stages.findByIdAndOrganizationIdAndArchivedAtIsNull(id,actor.getOrganizationId()).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));Checks.require(s.getPipelineId().equals(pipelineId),"Etapa y embudo no coinciden");String outcome=Checks.oneOf(r.outcome(),"ABIERTA","GANADA","PERDIDA");if(!outcome.equals(s.getOutcome()))Checks.require(opportunities.countByOrganizationIdAndStageIdAndArchivedAtIsNull(actor.getOrganizationId(),id)==0,"No se cambia el resultado de una etapa que contiene oportunidades");s.update(r.name(),r.position(),outcome);audit.log(actor,"UPDATED","STAGE",id);return StageView.from(s);}
}
