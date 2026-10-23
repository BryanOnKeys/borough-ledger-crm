package com.boroughledger.crm.pipelines;
import com.boroughledger.crm.shared.TenantRecord;
import jakarta.persistence.*;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity @Table(name="pipeline_stages")
public class PipelineStage extends TenantRecord {
 @JdbcTypeCode(SqlTypes.VARCHAR) @Column(name="pipeline_id",length=36,nullable=false) private UUID pipelineId;
 @Column(nullable=false,length=100) private String name;
 @Column(name="position_index",nullable=false) private int position;
 @Column(nullable=false,length=15) private String outcome;
 protected PipelineStage(){}
 public PipelineStage(UUID org,UUID pipelineId,String name,int position,String outcome){super(org);this.pipelineId=pipelineId;this.name=name;this.position=position;this.outcome=outcome;}
 public void update(String name,int position,String outcome){this.name=name;this.position=position;this.outcome=outcome;}
 public UUID getPipelineId(){return pipelineId;} public String getName(){return name;} public int getPosition(){return position;} public String getOutcome(){return outcome;}
}
