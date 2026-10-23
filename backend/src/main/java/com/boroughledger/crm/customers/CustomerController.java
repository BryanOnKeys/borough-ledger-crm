package com.boroughledger.crm.customers;

import com.boroughledger.crm.users.AppUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/customers") @Validated
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }
    @GetMapping
    public Page<CustomerDto.Response> list(@AuthenticationPrincipal AppUser actor,
             @RequestParam(defaultValue = "0") @Min(0) int page,
             @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size, @RequestParam(required=false) String search) {
        return service.list(actor, page, size, search);
    }
    @GetMapping("/{id}")
    public CustomerDto.Response get(@AuthenticationPrincipal AppUser actor, @PathVariable UUID id) {
        return service.get(actor, id);
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public ResponseEntity<CustomerDto.Response> create(@AuthenticationPrincipal AppUser actor,
                @Valid @RequestBody CustomerDto.Request request) {
        CustomerDto.Response created = service.create(actor, request);
        return ResponseEntity.created(URI.create("/api/v1/customers/" + created.id())).body(created);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES')")
    public CustomerDto.Response update(@AuthenticationPrincipal AppUser actor, @PathVariable UUID id,
                   @Valid @RequestBody CustomerDto.Request request) {
        return service.update(actor, id, request);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> archive(@AuthenticationPrincipal AppUser actor, @PathVariable UUID id) {
        service.archive(actor, id);
        return ResponseEntity.noContent().build();
    }
}
