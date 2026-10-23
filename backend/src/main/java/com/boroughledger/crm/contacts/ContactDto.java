package com.boroughledger.crm.contacts;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public final class ContactDto { private ContactDto(){}
 public record Request(
  @NotBlank @Size(max=100) String firstName,
  @NotBlank @Size(max=100) String lastName,
  @Email @Size(max=190) String email,
  @Size(max=40) String phone,
  @Size(max=130) String jobTitle,
   UUID customerId,
   UUID ownerId,
  @Size(max=2000) String notes
){}
 public record Response(UUID id,String firstName, String lastName, String email, String phone, String jobTitle, UUID customerId, UUID ownerId, String notes, LocalDateTime createdAt, LocalDateTime updatedAt, long version){
  public static Response from(Contact obj){ return new Response(obj.getId(),obj.getFirstName(), obj.getLastName(), obj.getEmail(), obj.getPhone(), obj.getJobTitle(), obj.getCustomerId(), obj.getOwnerId(), obj.getNotes(),obj.getCreatedAt(),obj.getUpdatedAt(),obj.getVersion()); }
 }
}
