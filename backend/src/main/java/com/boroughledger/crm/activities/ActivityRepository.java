package com.boroughledger.crm.activities;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ActivityRepository extends JpaRepository<Activity,UUID> {
 @org.springframework.data.jpa.repository.Query("select e from Activity e where e.organizationId=:org and e.archivedAt is null and (:search is null or lower(e.title) like lower(concat('%',:search,'%')))")
 Page<Activity> search(@org.springframework.data.repository.query.Param("org") UUID org,@org.springframework.data.repository.query.Param("search") String search,Pageable pageable);
 Page<Activity> findByOrganizationIdAndArchivedAtIsNull(UUID org, Pageable pageable);
 Optional<Activity> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id,UUID org);
}
