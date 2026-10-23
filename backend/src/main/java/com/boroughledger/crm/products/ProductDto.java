package com.boroughledger.crm.products;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public final class ProductDto { private ProductDto(){}
 public record Request(
  @NotBlank @Size(max=50) String sku,
  @NotBlank @Size(max=180) String name,
  @Size(max=2000) String description,
  @NotNull @DecimalMin("0.00") BigDecimal unitPrice,
  @NotBlank @Pattern(regexp="[A-Z]{3}") String currency,
  @NotBlank String kind,
  @NotNull Boolean active
){}
 public record Response(UUID id,String sku, String name, String description, BigDecimal unitPrice, String currency, String kind, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, long version){
  public static Response from(Product obj){ return new Response(obj.getId(),obj.getSku(), obj.getName(), obj.getDescription(), obj.getUnitPrice(), obj.getCurrency(), obj.getKind(), obj.getActive(),obj.getCreatedAt(),obj.getUpdatedAt(),obj.getVersion()); }
 }
}
