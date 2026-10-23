package com.boroughledger.crm.organizations;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrganizationRepository extends JpaRepository<Organization, UUID> { }
