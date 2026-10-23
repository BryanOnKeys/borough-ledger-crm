package com.boroughledger.crm.leads;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public final class LeadDto { private LeadDto(){}
 public record Request(
  @NotBlank @Size(max=180) String name,
  @Email @Size(max=190) String email,
  @Size(max=40) String phone,
  @Size(max=180) String company,
  @Size(max=80) String source,
  @NotBlank String status,
   UUID ownerId,
  @DecimalMin("0.00") BigDecimal expectedValue,
   LocalDateTime nextContactAt,
  @Size(max=2000) String notes
){}
 public record Response(UUID id,String name, String email, String phone, String company, String source, String status, UUID ownerId, BigDecimal expectedValue, LocalDateTime nextContactAt, String notes, LocalDateTime createdAt, LocalDateTime updatedAt, long version){
  public static Response from(Lead obj){ return new Response(obj.getId(),obj.getName(), obj.getEmail(), obj.getPhone(), obj.getCompany(), obj.getSource(), obj.getStatus(), obj.getOwnerId(), obj.getExpectedValue(), obj.getNextContactAt(), obj.getNotes(),obj.getCreatedAt(),obj.getUpdatedAt(),obj.getVersion()); }
 }
}
