package com.boroughledger.crm.quotations;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
public interface QuotationRepository extends JpaRepository<Quotation,UUID>{
 Page<Quotation> findByOrganizationIdAndArchivedAtIsNull(UUID org,Pageable pageable);
 Optional<Quotation> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id,UUID org);
}
