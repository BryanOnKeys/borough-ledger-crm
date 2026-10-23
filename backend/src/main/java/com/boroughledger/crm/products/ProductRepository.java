package com.boroughledger.crm.products;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product,UUID> {
 @org.springframework.data.jpa.repository.Query("select e from Product e where e.organizationId=:org and e.archivedAt is null and (:search is null or lower(e.name) like lower(concat('%',:search,'%')))")
 Page<Product> search(@org.springframework.data.repository.query.Param("org") UUID org,@org.springframework.data.repository.query.Param("search") String search,Pageable pageable);
 Page<Product> findByOrganizationIdAndArchivedAtIsNull(UUID org, Pageable pageable);
 Optional<Product> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id,UUID org);
}
