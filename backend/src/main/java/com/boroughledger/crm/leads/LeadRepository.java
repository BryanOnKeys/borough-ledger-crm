package com.boroughledger.crm.leads;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LeadRepository extends JpaRepository<Lead,UUID> {
 @org.springframework.data.jpa.repository.Query("select e from Lead e where e.organizationId=:org and e.archivedAt is null and (:search is null or lower(e.name) like lower(concat('%',:search,'%')))")
 Page<Lead> search(@org.springframework.data.repository.query.Param("org") UUID org,@org.springframework.data.repository.query.Param("search") String search,Pageable pageable);
 Page<Lead> findByOrganizationIdAndArchivedAtIsNull(UUID org, Pageable pageable);
 Optional<Lead> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id,UUID org);
}
