package com.boroughledger.crm.customers;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @org.springframework.data.jpa.repository.Query("select c from Customer c where c.organizationId=:org and c.archivedAt is null and (:search is null or lower(c.name) like lower(concat('%',:search,'%')) or lower(c.email) like lower(concat('%',:search,'%')))")
    Page<Customer> search(@org.springframework.data.repository.query.Param("org") UUID org, @org.springframework.data.repository.query.Param("search") String search, Pageable page);
    java.util.Optional<Customer> findFirstByOrganizationIdAndNameIgnoreCaseAndArchivedAtIsNull(UUID org,String name);
    Page<Customer> findByOrganizationIdAndArchivedAtIsNull(UUID organizationId, Pageable pageable);
    Optional<Customer> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id, UUID organizationId);
}
