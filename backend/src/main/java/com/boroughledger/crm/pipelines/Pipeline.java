package com.boroughledger.crm.pipelines;
import com.boroughledger.crm.shared.TenantRecord;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="pipelines")
public class Pipeline extends TenantRecord {
 @Column(name="name") private String name;
 @Column(name="description") private String description;
 @Column(name="active") private Boolean active;
 protected Pipeline() {}
 public Pipeline(UUID org, PipelineDto.Request request){ super(org); update(request); }
 public void update(PipelineDto.Request r){
  this.name=r.name();
  this.description=r.description();
  this.active=r.active();
 }
 public String getName(){return name;}
 public String getDescription(){return description;}
 public Boolean getActive(){return active;}
}
