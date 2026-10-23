package com.boroughledger.crm.quotations;
import com.boroughledger.crm.shared.TenantRecord;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="quotations")
public class Quotation extends TenantRecord {
 @Column(nullable=false,length=45,unique=true) private String number;
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="opportunity_id",length=36,nullable=false) private UUID opportunityId;
 @Column(nullable=false,length=15) private String status;
 @Column(nullable=false,length=3) private String currency;
 @Column(name="valid_until") private LocalDate validUntil;
 @Column(precision=17,scale=2,nullable=false) private BigDecimal subtotal;
 @Column(precision=17,scale=2,nullable=false) private BigDecimal discountTotal;
 @Column(precision=17,scale=2,nullable=false) private BigDecimal taxTotal;
 @Column(precision=17,scale=2,nullable=false) private BigDecimal total;
 @Column(name="internal_notes",length=2000) private String internalNotes;
 protected Quotation(){}
 public Quotation(UUID org,UUID opportunityId,String currency,LocalDate validUntil,String notes){super(org);this.number="COT-"+java.time.Year.now(java.time.ZoneOffset.UTC)+"-"+UUID.randomUUID().toString().substring(0,10).toUpperCase();this.opportunityId=opportunityId;this.currency=currency;this.validUntil=validUntil;this.status="BORRADOR";this.internalNotes=notes;totals(BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO);}
 public void totals(BigDecimal subtotal,BigDecimal discount,BigDecimal tax,BigDecimal total){this.subtotal=subtotal;this.discountTotal=discount;this.taxTotal=tax;this.total=total;}
 public void edit(LocalDate until,String notes){this.validUntil=until;this.internalNotes=notes;}
 public void status(String status){this.status=status;}
 public String getNumber(){return number;} public UUID getOpportunityId(){return opportunityId;}public String getStatus(){return status;}public String getCurrency(){return currency;}public LocalDate getValidUntil(){return validUntil;}public BigDecimal getSubtotal(){return subtotal;}public BigDecimal getDiscountTotal(){return discountTotal;}public BigDecimal getTaxTotal(){return taxTotal;}public BigDecimal getTotal(){return total;}public String getInternalNotes(){return internalNotes;}
}
