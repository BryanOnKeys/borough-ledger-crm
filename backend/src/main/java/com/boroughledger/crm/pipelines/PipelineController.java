package com.boroughledger.crm.pipelines;
import com.boroughledger.crm.users.AppUser;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/pipelines")
public class PipelineController {
 private final PipelineService service;
 public PipelineController(PipelineService service){this.service=service;}
 @GetMapping public Page<PipelineDto.Response> list(@AuthenticationPrincipal AppUser actor,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@RequestParam(required=false) String search){return service.list(actor,page,size,search);}
 @GetMapping("/{id}") public PipelineDto.Response get(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id){return service.get(actor,id);}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
 public ResponseEntity<PipelineDto.Response> create(@AuthenticationPrincipal AppUser actor,@Valid @RequestBody PipelineDto.Request r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(actor,r));}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
 public PipelineDto.Response update(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id,@Valid @RequestBody PipelineDto.Request r){return service.update(actor,id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
 public ResponseEntity<Void> archive(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id){service.archive(actor,id);return ResponseEntity.noContent().build();}
}
