package com.boroughledger.crm.shared;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@MappedSuperclass
public abstract class TenantRecord {
 @Id @JdbcTypeCode(SqlTypes.VARCHAR) @Column(length=36,nullable=false)
 protected UUID id;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="organization_id",length=36,nullable=false)
 protected UUID organizationId;
 @Column(name="created_at",nullable=false) protected LocalDateTime createdAt;
 @Column(name="updated_at",nullable=false) protected LocalDateTime updatedAt;
 @Column(name="archived_at") protected LocalDateTime archivedAt;
 @Version @Column(nullable=false) protected long version;
 protected TenantRecord() {}
 protected TenantRecord(UUID org) { id=UUID.randomUUID();organizationId=org;createdAt=LocalDateTime.now(ZoneOffset.UTC);updatedAt=createdAt;}
 @PreUpdate protected void touch() { updatedAt=LocalDateTime.now(ZoneOffset.UTC); }
 public void archive() { archivedAt=LocalDateTime.now(ZoneOffset.UTC); }
 public UUID getId(){return id;}
 public UUID getOrganizationId(){return organizationId;}
 public LocalDateTime getCreatedAt(){return createdAt;}
 public LocalDateTime getUpdatedAt(){return updatedAt;}
 public LocalDateTime getArchivedAt(){return archivedAt;}
 public long getVersion(){return version;}
}
