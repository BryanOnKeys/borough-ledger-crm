package com.boroughledger.crm.contacts;
import com.boroughledger.crm.shared.TenantRecord;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="contacts")
public class Contact extends TenantRecord {
 @Column(name="first_name") private String firstName;
 @Column(name="last_name") private String lastName;
 @Column(name="email") private String email;
 @Column(name="phone") private String phone;
 @Column(name="job_title") private String jobTitle;
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="customer_id", length=36) private UUID customerId;
 @JdbcTypeCode(SqlTypes.VARCHAR)
 @Column(name="owner_id", length=36) private UUID ownerId;
 @Column(name="notes") private String notes;
 protected Contact() {}
 public Contact(UUID org, ContactDto.Request request){ super(org); update(request); }
 public void update(ContactDto.Request r){
  this.firstName=r.firstName();
  this.lastName=r.lastName();
  this.email=r.email();
  this.phone=r.phone();
  this.jobTitle=r.jobTitle();
  this.customerId=r.customerId();
  this.ownerId=r.ownerId();
  this.notes=r.notes();
 }
 public String getFirstName(){return firstName;}
 public String getLastName(){return lastName;}
 public String getEmail(){return email;}
 public String getPhone(){return phone;}
 public String getJobTitle(){return jobTitle;}
 public UUID getCustomerId(){return customerId;}
 public UUID getOwnerId(){return ownerId;}
 public String getNotes(){return notes;}
}
