package com.boroughledger.crm.audit;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name = "audit_logs")
public class AuditLog {
    @Id @JdbcTypeCode(SqlTypes.VARCHAR) @Column(length = 36, nullable = false)
    private UUID id;
    @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name = "organization_id", nullable = false, length = 36)
    private UUID organizationId;
    @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name = "actor_user_id", nullable = false, length = 36)
    private UUID actorUserId;
    @Column(nullable = false, length = 40) private String action;
    @Column(name = "entity_type", nullable = false, length = 40) private String entityType;
    @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name = "entity_id", nullable = false, length = 36)
    private UUID entityId;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    public UUID getId(){return id;}
    public UUID getOrganizationId(){return organizationId;}
    public UUID getActorUserId(){return actorUserId;}
    public String getAction(){return action;}
    public String getEntityType(){return entityType;}
    public UUID getEntityId(){return entityId;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    protected AuditLog() { }
    public AuditLog(UUID org, UUID actor, String action, String entityType, UUID entityId) {
        this.id = UUID.randomUUID(); this.organizationId = org; this.actorUserId = actor;
        this.action = action; this.entityType = entityType; this.entityId = entityId;
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}
