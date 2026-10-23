package com.boroughledger.crm.leads;
import com.boroughledger.crm.users.AppUser;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/leads")
public class LeadController {
 private final LeadService service;
 public LeadController(LeadService service){this.service=service;}
 @GetMapping public Page<LeadDto.Response> list(@AuthenticationPrincipal AppUser actor,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@RequestParam(required=false) String search){return service.list(actor,page,size,search);}
 @GetMapping("/{id}") public LeadDto.Response get(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id){return service.get(actor,id);}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
 public ResponseEntity<LeadDto.Response> create(@AuthenticationPrincipal AppUser actor,@Valid @RequestBody LeadDto.Request r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(actor,r));}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
 public LeadDto.Response update(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id,@Valid @RequestBody LeadDto.Request r){return service.update(actor,id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
 public ResponseEntity<Void> archive(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id){service.archive(actor,id);return ResponseEntity.noContent().build();}
}
