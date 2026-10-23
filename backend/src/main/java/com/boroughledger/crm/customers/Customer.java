package com.boroughledger.crm.customers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name = "customers")
public class Customer {
    @Id @JdbcTypeCode(SqlTypes.VARCHAR) @Column(length = 36, nullable = false)
    private UUID id;
    @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name = "organization_id", length = 36, nullable = false)
    private UUID organizationId;
    @Column(length = 180, nullable = false) private String name;
    @Column(length = 190) private String email;
    @Column(length = 40) private String phone;
    @Column(length = 100) private String industry;
    @Column(length = 20, nullable = false) private String status;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @Column(name = "archived_at") private LocalDateTime archivedAt;
    @Version @Column(nullable = false) private long version;
    @Column(name="commercial_name",length=180) private String commercialName;
    @Column(name="tax_id",length=30) private String taxId;
    @Column(length=100) private String country;
    @Column(length=300) private String address;
    @Column(length=220) private String website;
    @Column(length=2000) private String description;
    @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="owner_id",length=36) private UUID ownerId;
    protected Customer() { }
    public Customer(UUID organizationId, String name, String email, String phone, String industry) {
        this.id = UUID.randomUUID(); this.organizationId = organizationId;
        this.name = name.trim(); this.email = email; this.phone = phone; this.industry = industry;
        this.status = "ACTIVE"; this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        this.updatedAt = this.createdAt;
    }
    public void update(String name, String email, String phone, String industry) {
        this.name = name.trim(); this.email = email; this.phone = phone; this.industry = industry;
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
    public void archive() { this.archivedAt = LocalDateTime.now(ZoneOffset.UTC); this.updatedAt = archivedAt; this.status = "ARCHIVED"; }
    public void details(String commercialName,String taxId,String country,String address,String website,String description,UUID ownerId){this.commercialName=commercialName;this.taxId=taxId;this.country=country;this.address=address;this.website=website;this.description=description;this.ownerId=ownerId;}
    public String getCommercialName(){return commercialName;} public String getTaxId(){return taxId;} public String getCountry(){return country;} public String getAddress(){return address;} public String getWebsite(){return website;} public String getDescription(){return description;} public UUID getOwnerId(){return ownerId;}
    public UUID getId() { return id; }
    public UUID getOrganizationId() { return organizationId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getIndustry() { return industry; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public long getVersion() { return version; }
}
