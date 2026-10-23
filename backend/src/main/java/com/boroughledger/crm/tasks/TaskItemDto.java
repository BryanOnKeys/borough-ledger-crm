package com.boroughledger.crm.tasks;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public final class TaskItemDto { private TaskItemDto(){}
 public record Request(
  @NotBlank @Size(max=180) String title,
  @Size(max=2000) String description,
  @NotBlank String status,
  @NotBlank String priority,
   LocalDateTime dueAt,
   LocalDateTime completedAt,
   UUID ownerId,
   UUID opportunityId
){}
 public record Response(UUID id,String title, String description, String status, String priority, LocalDateTime dueAt, LocalDateTime completedAt, UUID ownerId, UUID opportunityId, LocalDateTime createdAt, LocalDateTime updatedAt, long version){
  public static Response from(TaskItem obj){ return new Response(obj.getId(),obj.getTitle(), obj.getDescription(), obj.getStatus(), obj.getPriority(), obj.getDueAt(), obj.getCompletedAt(), obj.getOwnerId(), obj.getOpportunityId(),obj.getCreatedAt(),obj.getUpdatedAt(),obj.getVersion()); }
 }
}
