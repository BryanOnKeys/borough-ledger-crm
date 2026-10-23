package com.boroughledger.crm.pipelines;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public final class PipelineDto { private PipelineDto(){}
 public record Request(
  @NotBlank @Size(max=120) String name,
  @Size(max=500) String description,
  @NotNull Boolean active
){}
 public record Response(UUID id,String name, String description, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, long version){
  public static Response from(Pipeline obj){ return new Response(obj.getId(),obj.getName(), obj.getDescription(), obj.getActive(),obj.getCreatedAt(),obj.getUpdatedAt(),obj.getVersion()); }
 }
}
