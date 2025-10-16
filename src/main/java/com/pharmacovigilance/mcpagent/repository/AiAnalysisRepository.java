package com.pharmacovigilance.mcpagent.repository;

import com.pharmacovigilance.mcpagent.model.AdverseEvent;
import com.pharmacovigilance.mcpagent.model.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, Long> {
    
    List<AiAnalysis> findByAdverseEvent(AdverseEvent adverseEvent);
    
    List<AiAnalysis> findByAnalysisType(AiAnalysis.AnalysisType analysisType);
    
    List<AiAnalysis> findByStatus(AiAnalysis.AnalysisStatus status);
    
    List<AiAnalysis> findByModelUsed(String modelUsed);
}


