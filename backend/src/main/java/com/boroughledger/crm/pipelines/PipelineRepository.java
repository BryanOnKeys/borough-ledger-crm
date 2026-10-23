package com.boroughledger.crm.pipelines;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PipelineRepository extends JpaRepository<Pipeline,UUID> {
 @org.springframework.data.jpa.repository.Query("select e from Pipeline e where e.organizationId=:org and e.archivedAt is null and (:search is null or lower(e.name) like lower(concat('%',:search,'%')))")
 Page<Pipeline> search(@org.springframework.data.repository.query.Param("org") UUID org,@org.springframework.data.repository.query.Param("search") String search,Pageable pageable);
 Page<Pipeline> findByOrganizationIdAndArchivedAtIsNull(UUID org, Pageable pageable);
 Optional<Pipeline> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id,UUID org);
}
