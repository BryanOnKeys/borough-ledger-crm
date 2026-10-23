package com.boroughledger.crm.quotations;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface QuotationLineRepository extends JpaRepository<QuotationLine,UUID>{
 List<QuotationLine> findByQuotationIdOrderById(UUID quote);
 void deleteByQuotationId(UUID quote);
}
