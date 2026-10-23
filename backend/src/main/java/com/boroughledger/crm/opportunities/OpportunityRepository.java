package com.boroughledger.crm.opportunities;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import java.util.*;
public interface OpportunityRepository extends JpaRepository<Opportunity,UUID> {
 Page<Opportunity> findByOrganizationIdAndArchivedAtIsNull(UUID org,Pageable pageable);
 Optional<Opportunity> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id,UUID org);
 long countByOrganizationIdAndStatusAndArchivedAtIsNull(UUID org,String status);
 @Query("select coalesce(sum(o.amount),0) from Opportunity o where o.organizationId=:org and o.status=:status and o.archivedAt is null and o.currency=:currency")
 java.math.BigDecimal amountByStatus(@Param("org") UUID org,@Param("status") String status,@Param("currency") String currency);
 long countByOrganizationIdAndStageIdAndArchivedAtIsNull(UUID org, UUID stage);
}
