package com.boroughledger.crm.organizations;
import com.boroughledger.crm.users.AppUser;
import com.boroughledger.crm.shared.AuditService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/organization")
public class OrganizationController {
 private final OrganizationRepository orgs;private final AuditService audit;
 public OrganizationController(OrganizationRepository orgs,AuditService audit){this.orgs=orgs;this.audit=audit;}
 public record Info(java.util.UUID id,String name){}
 public record Rename(@NotBlank @Size(max=160) String name){}
 @GetMapping @Transactional(readOnly=true) public Info get(@AuthenticationPrincipal AppUser a){Organization o=orgs.findById(a.getOrganizationId()).orElseThrow();return new Info(o.getId(),o.getName());}
 @PatchMapping @PreAuthorize("hasRole('ADMIN')") @Transactional public Info rename(@AuthenticationPrincipal AppUser a,@Valid @RequestBody Rename r){Organization o=orgs.findById(a.getOrganizationId()).orElseThrow();o.rename(r.name());audit.log(a,"RENAMED","ORGANIZATION",o.getId());return new Info(o.getId(),o.getName());}
}
