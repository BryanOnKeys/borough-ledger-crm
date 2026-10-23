package com.boroughledger.crm.products;
import com.boroughledger.crm.shared.TenantRecord;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="products")
public class Product extends TenantRecord {
 @Column(name="sku") private String sku;
 @Column(name="name") private String name;
 @Column(name="description") private String description;
 @Column(name="unit_price") private BigDecimal unitPrice;
 @Column(name="currency") private String currency;
 @Column(name="kind") private String kind;
 @Column(name="active") private Boolean active;
 protected Product() {}
 public Product(UUID org, ProductDto.Request request){ super(org); update(request); }
 public void update(ProductDto.Request r){
  this.sku=r.sku();
  this.name=r.name();
  this.description=r.description();
  this.unitPrice=r.unitPrice();
  this.currency=r.currency();
  this.kind=r.kind();
  this.active=r.active();
 }
 public String getSku(){return sku;}
 public String getName(){return name;}
 public String getDescription(){return description;}
 public BigDecimal getUnitPrice(){return unitPrice;}
 public String getCurrency(){return currency;}
 public String getKind(){return kind;}
 public Boolean getActive(){return active;}
}
