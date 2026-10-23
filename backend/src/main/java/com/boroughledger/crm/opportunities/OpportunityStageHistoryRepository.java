package com.boroughledger.crm.opportunities;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OpportunityStageHistoryRepository extends JpaRepository<OpportunityStageHistory,UUID> {
 List<OpportunityStageHistory> findByOrganizationIdAndOpportunityIdOrderByChangedAtDesc(UUID org,UUID opportunityId);
}
