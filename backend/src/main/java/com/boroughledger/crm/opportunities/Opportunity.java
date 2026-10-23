package com.boroughledger.crm.opportunities;
import com.boroughledger.crm.shared.TenantRecord;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="opportunities")
public class Opportunity extends TenantRecord {
 @Column(nullable=false,length=180) private String title;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="customer_id",nullable=false,length=36) private UUID customerId;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="contact_id",length=36) private UUID contactId;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="owner_id",length=36) private UUID ownerId;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="pipeline_id",nullable=false,length=36) private UUID pipelineId;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="stage_id",nullable=false,length=36) private UUID stageId;
 @Column(nullable=false,precision=17,scale=2) private BigDecimal amount;
 @Column(nullable=false,length=3) private String currency;
 @Column(nullable=false,length=15) private String status;
 @Column(name="expected_close") private LocalDate expectedClose;
 @Column(name="closed_at") private LocalDate closedAt;
 @Column(name="loss_reason",length=500) private String lossReason;
 @Column(length=2000) private String description;
 protected Opportunity(){}
 public Opportunity(UUID org,String title,UUID customerId,UUID contactId,UUID ownerId,UUID pipelineId,UUID stageId,BigDecimal amount,String currency,LocalDate expectedClose,String description,String status){ super(org);this.title=title;this.customerId=customerId;this.contactId=contactId;this.ownerId=ownerId;this.pipelineId=pipelineId;this.stageId=stageId;this.amount=amount;this.currency=currency;this.expectedClose=expectedClose;this.description=description;this.status=status; }
 public void update(String title,UUID contactId,UUID ownerId,BigDecimal amount,String currency,LocalDate expectedClose,String description){this.title=title;this.contactId=contactId;this.ownerId=ownerId;this.amount=amount;this.currency=currency;this.expectedClose=expectedClose;this.description=description;}
 public void move(UUID nextStage,String outcome,String loss){this.stageId=nextStage;this.status=switch(outcome){case "GANADA"->"GANADA";case "PERDIDA"->"PERDIDA";default->"ABIERTA";};this.closedAt="ABIERTA".equals(status)?null:LocalDate.now(java.time.ZoneOffset.UTC);this.lossReason="PERDIDA".equals(status)?loss:null;}
 public UUID getCustomerId(){return customerId;} public UUID getContactId(){return contactId;} public UUID getOwnerId(){return ownerId;} public UUID getPipelineId(){return pipelineId;} public UUID getStageId(){return stageId;} public String getTitle(){return title;} public BigDecimal getAmount(){return amount;} public String getCurrency(){return currency;} public String getStatus(){return status;} public LocalDate getExpectedClose(){return expectedClose;} public LocalDate getClosedAt(){return closedAt;} public String getLossReason(){return lossReason;} public String getDescription(){return description;}
}
