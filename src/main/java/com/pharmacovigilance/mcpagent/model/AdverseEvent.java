package com.pharmacovigilance.mcpagent.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "adverse_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdverseEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String caseNumber;
    
    @Column(nullable = false)
    private String drugName;
    
    @Column(nullable = false)
    private String adverseEventDescription;
    
    @Enumerated(EnumType.STRING)
    private SeverityLevel severity;
    
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    
    @Column(length = 1000)
    private String symptoms;
    
    @Column(length = 1000)
    private String medicalHistory;
    
    @Column(length = 1000)
    private String concomitantMedications;
    
    @Column(length = 1000)
    private String reporterNotes;
    
    @Column(length = 1000)
    private String aiAnalysis;
    
    @Column(length = 1000)
    private String riskAssessment;
    
    @Enumerated(EnumType.STRING)
    private CausalityAssessment causality;
    
    @Column
    private LocalDateTime eventDate;
    
    @Column
    private LocalDateTime reportDate;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id")
    private Drug drug;
    
    @OneToMany(mappedBy = "adverseEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FollowUpAction> followUpActions;
    
    public enum SeverityLevel {
        MILD, MODERATE, SEVERE, LIFE_THREATENING, FATAL
    }
    
    public enum EventStatus {
        NEW, UNDER_INVESTIGATION, CONFIRMED, REJECTED, CLOSED
    }
    
    public enum CausalityAssessment {
        CERTAIN, PROBABLE, POSSIBLE, UNLIKELY, UNCLASSIFIABLE, UNASSESSABLE
    }
}
