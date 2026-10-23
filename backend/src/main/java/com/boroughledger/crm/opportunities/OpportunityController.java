package com.boroughledger.crm.opportunities;
import com.boroughledger.crm.users.AppUser;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/opportunities")
public class OpportunityController {
 private final OpportunityService service;
 public OpportunityController(OpportunityService service){this.service=service;}
 @GetMapping public Page<OpportunityService.View> list(@AuthenticationPrincipal AppUser a,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.list(a,page,size);}
 @GetMapping("/{id}") public OpportunityService.View get(@AuthenticationPrincipal AppUser a,@PathVariable UUID id){return service.get(a,id);}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')") public ResponseEntity<OpportunityService.View> create(@AuthenticationPrincipal AppUser a,@Valid @RequestBody OpportunityService.Input r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(a,r));}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')") public OpportunityService.View update(@AuthenticationPrincipal AppUser a,@PathVariable UUID id,@Valid @RequestBody OpportunityService.Edit r){return service.update(a,id,r);}
 @PatchMapping("/{id}/stage") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')") public OpportunityService.View move(@AuthenticationPrincipal AppUser a,@PathVariable UUID id,@Valid @RequestBody OpportunityService.Move r){return service.move(a,id,r);}
 @GetMapping("/{id}/history") public List<OpportunityStageHistory> history(@AuthenticationPrincipal AppUser a,@PathVariable UUID id){return service.history(a,id);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") public ResponseEntity<Void> archive(@AuthenticationPrincipal AppUser a,@PathVariable UUID id){service.archive(a,id);return ResponseEntity.noContent().build();}
}
