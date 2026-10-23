package com.boroughledger.crm.contacts;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ContactRepository extends JpaRepository<Contact,UUID> {
 @org.springframework.data.jpa.repository.Query("select e from Contact e where e.organizationId=:org and e.archivedAt is null and (:search is null or lower(e.lastName) like lower(concat('%',:search,'%')))")
 Page<Contact> search(@org.springframework.data.repository.query.Param("org") UUID org,@org.springframework.data.repository.query.Param("search") String search,Pageable pageable);
 Page<Contact> findByOrganizationIdAndArchivedAtIsNull(UUID org, Pageable pageable);
 Optional<Contact> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id,UUID org);
 Optional<Contact> findFirstByOrganizationIdAndEmailIgnoreCaseAndArchivedAtIsNull(UUID org,String email);
}
