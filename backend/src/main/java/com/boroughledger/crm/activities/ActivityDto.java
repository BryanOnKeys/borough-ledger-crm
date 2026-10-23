package com.boroughledger.crm.activities;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public final class ActivityDto { private ActivityDto(){}
 public record Request(
  @NotBlank @Size(max=180) String title,
  @NotBlank String kind,
   LocalDateTime dueAt,
   LocalDateTime completedAt,
   UUID customerId,
   UUID contactId,
   UUID opportunityId,
   UUID ownerId,
  @Size(max=2000) String details
){}
 public record Response(UUID id,String title, String kind, LocalDateTime dueAt, LocalDateTime completedAt, UUID customerId, UUID contactId, UUID opportunityId, UUID ownerId, String details, LocalDateTime createdAt, LocalDateTime updatedAt, long version){
  public static Response from(Activity obj){ return new Response(obj.getId(),obj.getTitle(), obj.getKind(), obj.getDueAt(), obj.getCompletedAt(), obj.getCustomerId(), obj.getContactId(), obj.getOpportunityId(), obj.getOwnerId(), obj.getDetails(),obj.getCreatedAt(),obj.getUpdatedAt(),obj.getVersion()); }
 }
}
