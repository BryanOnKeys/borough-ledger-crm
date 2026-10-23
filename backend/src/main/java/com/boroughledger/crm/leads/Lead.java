package com.boroughledger.crm.leads;
import com.boroughledger.crm.shared.TenantRecord;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="leads")
public class Lead extends TenantRecord {
 @Column(name="name") private String name;
 @Column(name="email") private String email;
 @Column(name="phone") private String phone;
 @Column(name="company") private String company;
 @Column(name="source") private String source;
 @Column(name="status") private String status;
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="owner_id", length=36) private UUID ownerId;
 @Column(name="expected_value") private BigDecimal expectedValue;
 @Column(name="next_contact_at") private LocalDateTime nextContactAt;
 @Column(name="notes") private String notes;
 protected Lead() {}
 public Lead(UUID org, LeadDto.Request request){ super(org); update(request); }
 public void update(LeadDto.Request r){
  this.name=r.name();
  this.email=r.email();
  this.phone=r.phone();
  this.company=r.company();
  this.source=r.source();
  this.status=r.status();
  this.ownerId=r.ownerId();
  this.expectedValue=r.expectedValue();
  this.nextContactAt=r.nextContactAt();
  this.notes=r.notes();
 }
 public void setConverted(){this.status="CONVERTIDO";}
 public String getName(){return name;}
 public String getEmail(){return email;}
 public String getPhone(){return phone;}
 public String getCompany(){return company;}
 public String getSource(){return source;}
 public String getStatus(){return status;}
 public UUID getOwnerId(){return ownerId;}
 public BigDecimal getExpectedValue(){return expectedValue;}
 public LocalDateTime getNextContactAt(){return nextContactAt;}
 public String getNotes(){return notes;}
}
