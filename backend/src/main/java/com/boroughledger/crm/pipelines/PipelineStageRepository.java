package com.boroughledger.crm.pipelines;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PipelineStageRepository extends JpaRepository<PipelineStage,UUID> {
 List<PipelineStage> findByOrganizationIdAndPipelineIdAndArchivedAtIsNullOrderByPositionAsc(UUID org,UUID pipelineId);
 Optional<PipelineStage> findByIdAndOrganizationIdAndArchivedAtIsNull(UUID id,UUID org);
 long countByOrganizationIdAndPipelineIdAndArchivedAtIsNull(UUID org,UUID pipelineId);
}
