package com.boroughledger.crm.customers;

import com.boroughledger.crm.audit.AuditLog;
import com.boroughledger.crm.audit.AuditRepository;
import com.boroughledger.crm.users.AppUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {
    private final CustomerRepository customers;
    private final AuditRepository audit;
    public CustomerService(CustomerRepository customers, AuditRepository audit) {
        this.customers = customers; this.audit = audit;
    }
    @Transactional(readOnly = true)
    public Page<CustomerDto.Response> list(AppUser actor, int page, int size, String search) {
        PageRequest request = PageRequest.of(page, Math.min(size, 50), Sort.by("createdAt").descending());
        return customers.search(actor.getOrganizationId(), search == null || search.isBlank() ? null : search.strip(), request)
                .map(CustomerDto.Response::from);
    }
    @Transactional(readOnly = true)
    public CustomerDto.Response get(AppUser actor, UUID id) { return CustomerDto.Response.from(find(actor, id)); }
    @Transactional
    public CustomerDto.Response create(AppUser actor, CustomerDto.Request input) {
        Customer c = customers.save(new Customer(actor.getOrganizationId(), input.name(), input.email(), input.phone(), input.industry()));
        audit.save(new AuditLog(actor.getOrganizationId(), actor.getId(), "CREATED", "CUSTOMER", c.getId()));
        return CustomerDto.Response.from(c);
    }
    @Transactional
    public CustomerDto.Response update(AppUser actor, UUID id, CustomerDto.Request input) {
        Customer c = find(actor, id);
        c.update(input.name(), input.email(), input.phone(), input.industry());
        audit.save(new AuditLog(actor.getOrganizationId(), actor.getId(), "UPDATED", "CUSTOMER", c.getId()));
        return CustomerDto.Response.from(c);
    }
    @Transactional
    public void archive(AppUser actor, UUID id) {
        Customer c = find(actor, id);
        c.archive();
        audit.save(new AuditLog(actor.getOrganizationId(), actor.getId(), "ARCHIVED", "CUSTOMER", c.getId()));
    }
    private Customer find(AppUser actor, UUID id) {
        // Tenant isolation at query level; foreign organization is indistinguishable from a nonexistent customer.
        return customers.findByIdAndOrganizationIdAndArchivedAtIsNull(id, actor.getOrganizationId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }
}
