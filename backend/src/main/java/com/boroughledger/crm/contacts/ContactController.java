package com.boroughledger.crm.contacts;
import com.boroughledger.crm.users.AppUser;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/contacts")
public class ContactController {
 private final ContactService service;
 public ContactController(ContactService service){this.service=service;}
 @GetMapping public Page<ContactDto.Response> list(@AuthenticationPrincipal AppUser actor,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@RequestParam(required=false) String search){return service.list(actor,page,size,search);}
 @GetMapping("/{id}") public ContactDto.Response get(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id){return service.get(actor,id);}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
 public ResponseEntity<ContactDto.Response> create(@AuthenticationPrincipal AppUser actor,@Valid @RequestBody ContactDto.Request r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(actor,r));}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES')")
 public ContactDto.Response update(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id,@Valid @RequestBody ContactDto.Request r){return service.update(actor,id,r);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
 public ResponseEntity<Void> archive(@AuthenticationPrincipal AppUser actor,@PathVariable UUID id){service.archive(actor,id);return ResponseEntity.noContent().build();}
}
