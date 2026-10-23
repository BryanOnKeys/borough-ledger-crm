package com.boroughledger.crm.quotations;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="quotation_items")
public class QuotationLine {
 @Id @JdbcTypeCode(SqlTypes.VARCHAR) @Column(length=36) private UUID id;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="quotation_id",length=36,nullable=false) private UUID quotationId;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="product_id",length=36,nullable=false) private UUID productId;
 @Column(name="product_name",length=180,nullable=false) private String productName;
 @Column(name="sku_snapshot",length=50,nullable=false) private String skuSnapshot;
 @Column(name="unit_price",precision=17,scale=2,nullable=false) private BigDecimal unitPrice;
 @Column(precision=15,scale=3,nullable=false) private BigDecimal quantity;
 @Column(name="discount_percent",precision=5,scale=2,nullable=false) private BigDecimal discountPercent;
 @Column(name="tax_percent",precision=5,scale=2,nullable=false) private BigDecimal taxPercent;
 @Column(name="line_total",precision=17,scale=2,nullable=false) private BigDecimal lineTotal;
 protected QuotationLine(){}
 public QuotationLine(UUID quotationId,UUID productId,String productName,String sku,BigDecimal unitPrice,BigDecimal quantity,BigDecimal discountPercent,BigDecimal taxPercent,BigDecimal total){id=UUID.randomUUID();this.quotationId=quotationId;this.productId=productId;this.productName=productName;this.skuSnapshot=sku;this.unitPrice=unitPrice;this.quantity=quantity;this.discountPercent=discountPercent;this.taxPercent=taxPercent;this.lineTotal=total;}
 public UUID getId(){return id;}public UUID getProductId(){return productId;} public String getProductName(){return productName;}public String getSkuSnapshot(){return skuSnapshot;}public BigDecimal getUnitPrice(){return unitPrice;}public BigDecimal getQuantity(){return quantity;}public BigDecimal getDiscountPercent(){return discountPercent;}public BigDecimal getTaxPercent(){return taxPercent;}public BigDecimal getLineTotal(){return lineTotal;}
}
