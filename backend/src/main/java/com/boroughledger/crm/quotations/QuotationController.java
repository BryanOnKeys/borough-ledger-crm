package com.boroughledger.crm.quotations;
import com.boroughledger.crm.users.AppUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/quotations") public class QuotationController {
 private final QuotationService service;public QuotationController(QuotationService service){this.service=service;}
 public record StatusChange(@NotBlank String status){}
 @GetMapping public Page<QuotationService.View> list(@AuthenticationPrincipal AppUser a,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.list(a,page,size);}
 @GetMapping("/{id}") public QuotationService.View get(@AuthenticationPrincipal AppUser a,@PathVariable UUID id){return service.get(a,id);}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')") public ResponseEntity<QuotationService.View> create(@AuthenticationPrincipal AppUser a,@Valid @RequestBody QuotationService.Input r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(a,r));}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')") public QuotationService.View revise(@AuthenticationPrincipal AppUser a,@PathVariable UUID id,@Valid @RequestBody QuotationService.Revision r){return service.revise(a,id,r);}
 @PatchMapping("/{id}/status") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')") public QuotationService.View change(@AuthenticationPrincipal AppUser a,@PathVariable UUID id,@Valid @RequestBody StatusChange r){return service.setStatus(a,id,r.status());}
}
