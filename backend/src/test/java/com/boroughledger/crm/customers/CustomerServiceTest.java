package com.boroughledger.crm.customers;

import com.boroughledger.crm.audit.AuditLog;
import com.boroughledger.crm.audit.AuditRepository;
import com.boroughledger.crm.users.AppUser;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock CustomerRepository customers;
    @Mock AuditRepository audit;
    CustomerService service;
    AppUser actor;

    @BeforeEach void setup() {
        service = new CustomerService(customers, audit);
        actor = new AppUser(UUID.randomUUID(), "admin@example.test", "hashed", "Admin", "ADMIN");
    }
    @Test void creationUsesAuthenticatedTenantAndWritesAudit() {
        when(customers.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var saved = service.create(actor, new CustomerDto.Request("  ACME  ", "contact@example.test", null, null));
        assertEquals("ACME", saved.name());
        verify(customers).save(argThat(c -> c.getOrganizationId().equals(actor.getOrganizationId())));
        verify(audit).save(any(AuditLog.class));
    }
    @Test void foreignTenantIsNotObservable() {
        UUID id = UUID.randomUUID();
        when(customers.findByIdAndOrganizationIdAndArchivedAtIsNull(id, actor.getOrganizationId()))
                .thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.get(actor, id));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
