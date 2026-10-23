package com.boroughledger.crm.activities;
import com.boroughledger.crm.shared.TenantRecord;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="activities")
public class Activity extends TenantRecord {
 @Column(name="title") private String title;
 @Column(name="kind") private String kind;
 @Column(name="due_at") private LocalDateTime dueAt;
 @Column(name="completed_at") private LocalDateTime completedAt;
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="customer_id", length=36) private UUID customerId;
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="contact_id", length=36) private UUID contactId;
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="opportunity_id", length=36) private UUID opportunityId;
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="owner_id", length=36) private UUID ownerId;
 @Column(name="details") private String details;
 protected Activity() {}
 public Activity(UUID org, ActivityDto.Request request){ super(org); update(request); }
 public void update(ActivityDto.Request r){
  this.title=r.title();
  this.kind=r.kind();
  this.dueAt=r.dueAt();
  this.completedAt=r.completedAt();
  this.customerId=r.customerId();
  this.contactId=r.contactId();
  this.opportunityId=r.opportunityId();
  this.ownerId=r.ownerId();
  this.details=r.details();
 }
 public String getTitle(){return title;}
 public String getKind(){return kind;}
 public LocalDateTime getDueAt(){return dueAt;}
 public LocalDateTime getCompletedAt(){return completedAt;}
 public UUID getCustomerId(){return customerId;}
 public UUID getContactId(){return contactId;}
 public UUID getOpportunityId(){return opportunityId;}
 public UUID getOwnerId(){return ownerId;}
 public String getDetails(){return details;}
}
