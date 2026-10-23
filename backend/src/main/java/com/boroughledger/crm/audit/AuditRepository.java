package com.boroughledger.crm.audit;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditRepository extends JpaRepository<AuditLog, UUID> {
 org.springframework.data.domain.Page<AuditLog> findByOrganizationId(UUID org,org.springframework.data.domain.Pageable page);
}
