package com.pharmacovigilance.mcpagent.repository;

import com.pharmacovigilance.mcpagent.model.AdverseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdverseEventRepository extends JpaRepository<AdverseEvent, Long> {
    
    List<AdverseEvent> findBySeverity(AdverseEvent.SeverityLevel severity);
    
    List<AdverseEvent> findByStatus(AdverseEvent.EventStatus status);
    
    List<AdverseEvent> findBySeverityAndStatus(AdverseEvent.SeverityLevel severity, AdverseEvent.EventStatus status);
    
    List<AdverseEvent> findByDrugNameContainingIgnoreCase(String drugName);
    
    List<AdverseEvent> findByPatientPatientId(String patientId);
    
    List<AdverseEvent> findBySeverityAndStatusAndDrugName(
        AdverseEvent.SeverityLevel severity, 
        AdverseEvent.EventStatus status, 
        String drugName
    );
    
    @Query("SELECT ae FROM AdverseEvent ae WHERE ae.eventDate >= :startDate AND ae.eventDate <= :endDate")
    List<AdverseEvent> findByEventDateRange(@Param("startDate") java.time.LocalDateTime startDate, 
                                           @Param("endDate") java.time.LocalDateTime endDate);
    
    @Query("SELECT ae FROM AdverseEvent ae WHERE ae.causality = :causality")
    List<AdverseEvent> findByCausality(@Param("causality") AdverseEvent.CausalityAssessment causality);
    
    @Query("SELECT COUNT(ae) FROM AdverseEvent ae WHERE ae.severity = :severity")
    Long countBySeverity(@Param("severity") AdverseEvent.SeverityLevel severity);
    
    @Query("SELECT COUNT(ae) FROM AdverseEvent ae WHERE ae.status = :status")
    Long countByStatus(@Param("status") AdverseEvent.EventStatus status);
}
