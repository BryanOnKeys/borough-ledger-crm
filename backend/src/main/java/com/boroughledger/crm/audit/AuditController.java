package com.boroughledger.crm.audit;
import com.boroughledger.crm.users.AppUser;
import com.boroughledger.crm.shared.Checks;
import java.util.UUID;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/audit") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class AuditController {
 private final AuditRepository repository;public AuditController(AuditRepository repository){this.repository=repository;}
 public record View(UUID id,UUID actorId,String action,String entityType,UUID entityId,LocalDateTime createdAt){static View from(AuditLog a){return new View(a.getId(),a.getActorUserId(),a.getAction(),a.getEntityType(),a.getEntityId(),a.getCreatedAt());}}
 @GetMapping public Page<View> list(@AuthenticationPrincipal AppUser a,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return repository.findByOrganizationId(a.getOrganizationId(),Checks.page(page,size)).map(View::from);}
}
