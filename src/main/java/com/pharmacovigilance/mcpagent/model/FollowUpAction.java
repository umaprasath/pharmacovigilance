package com.pharmacovigilance.mcpagent.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "follow_up_actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpAction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adverse_event_id", nullable = false)
    private AdverseEvent adverseEvent;
    
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ActionStatus status;
    
    @Column
    private String assignedTo;
    
    @Column
    private LocalDateTime dueDate;
    
    @Column
    private LocalDateTime completedDate;
    
    @Column(length = 1000)
    private String notes;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum ActionType {
        INVESTIGATION, REGULATORY_SUBMISSION, PATIENT_FOLLOW_UP, 
        DRUG_SAFETY_REVIEW, CLINICAL_TRIAL_REVIEW, LITERATURE_REVIEW
    }
    
    public enum ActionStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, OVERDUE
    }
}


