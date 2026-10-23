package com.boroughledger.crm.users;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
 boolean existsByIdAndOrganizationIdAndEnabledTrue(UUID id, UUID organizationId);
 org.springframework.data.domain.Page<AppUser> findByOrganizationId(UUID org,org.springframework.data.domain.Pageable page);
}
