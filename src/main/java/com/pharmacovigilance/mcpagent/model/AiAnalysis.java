package com.pharmacovigilance.mcpagent.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adverse_event_id", nullable = false)
    private AdverseEvent adverseEvent;
    
    @Column(length = 2000)
    private String analysisPrompt;
    
    @Column(length = 5000)
    private String aiResponse;
    
    @Column
    private String modelUsed;
    
    @Column
    private Double confidenceScore;
    
    @Enumerated(EnumType.STRING)
    private AnalysisType analysisType;
    
    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;
    
    @Column(length = 1000)
    private String extractedInsights;
    
    @Column(length = 1000)
    private String recommendations;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    public enum AnalysisType {
        CAUSALITY_ASSESSMENT, RISK_ANALYSIS, PATTERN_DETECTION, 
        REGULATORY_COMPLIANCE, CLINICAL_SIGNIFICANCE
    }
    
    public enum AnalysisStatus {
        PENDING, COMPLETED, FAILED, PARTIAL
    }
}


