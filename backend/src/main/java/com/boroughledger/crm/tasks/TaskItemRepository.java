package com.boroughledger.crm.tasks;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TaskItemRepository extends JpaRepository<TaskItem,UUID> {
 @org.springframework.data.jpa.repository.Query("select e from TaskItem e where e.organizationId=:org and e.archivedAt is null and (:search is null or lower(e.title) like lower(concat('%',:search,'%')))")
 Page<TaskItem> search(@org.springframework.data.repository.query.Param("org") UUID org,@org.springframework.data.repository.query.Param("search") String search,Pageable pageable);
 Page<TaskItem> findByOrganizationIdAndArchivedAtIsNull(UUID org, Pageable pageable);
 Optional<TaskItem> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id,UUID org);
}
