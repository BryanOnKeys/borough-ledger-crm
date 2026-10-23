package com.boroughledger.crm.opportunities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="opportunity_stage_history")
public class OpportunityStageHistory {
 @Id @JdbcTypeCode(SqlTypes.VARCHAR) @Column(length=36) private UUID id;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="organization_id",length=36) private UUID organizationId;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="opportunity_id",length=36) private UUID opportunityId;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="from_stage_id",length=36) private UUID fromStageId;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="to_stage_id",length=36) private UUID toStageId;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="actor_id",length=36) private UUID actorId;
 @Column(name="changed_at",nullable=false) private LocalDateTime changedAt;
 protected OpportunityStageHistory(){}
 public OpportunityStageHistory(UUID org,UUID opp,UUID from,UUID to,UUID actor){id=UUID.randomUUID();organizationId=org;opportunityId=opp;fromStageId=from;toStageId=to;actorId=actor;changedAt=LocalDateTime.now(ZoneOffset.UTC);}
 public UUID getFromStageId(){return fromStageId;}public UUID getToStageId(){return toStageId;}public UUID getActorId(){return actorId;}public LocalDateTime getChangedAt(){return changedAt;}
}
