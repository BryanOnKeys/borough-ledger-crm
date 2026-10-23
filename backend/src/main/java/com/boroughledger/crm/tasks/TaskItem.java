package com.boroughledger.crm.tasks;
import com.boroughledger.crm.shared.TenantRecord;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="crm_tasks")
public class TaskItem extends TenantRecord {
 @Column(name="title") private String title;
 @Column(name="description") private String description;
 @Column(name="status") private String status;
 @Column(name="priority") private String priority;
 @Column(name="due_at") private LocalDateTime dueAt;
 @Column(name="completed_at") private LocalDateTime completedAt;
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="owner_id", length=36) private UUID ownerId;
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="opportunity_id", length=36) private UUID opportunityId;
 protected TaskItem() {}
 public TaskItem(UUID org, TaskItemDto.Request request){ super(org); update(request); }
 public void update(TaskItemDto.Request r){
  this.title=r.title();
  this.description=r.description();
  this.status=r.status();
  this.priority=r.priority();
  this.dueAt=r.dueAt();
  this.completedAt=r.completedAt();
  this.ownerId=r.ownerId();
  this.opportunityId=r.opportunityId();
 }
 public String getTitle(){return title;}
 public String getDescription(){return description;}
 public String getStatus(){return status;}
 public String getPriority(){return priority;}
 public LocalDateTime getDueAt(){return dueAt;}
 public LocalDateTime getCompletedAt(){return completedAt;}
 public UUID getOwnerId(){return ownerId;}
 public UUID getOpportunityId(){return opportunityId;}
}
