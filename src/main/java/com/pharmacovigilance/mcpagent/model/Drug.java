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
@Table(name = "drugs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Drug {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String drugCode;
    
    @Column(nullable = false)
    private String drugName;
    
    @Column
    private String genericName;
    
    @Column
    private String manufacturer;
    
    @Column
    private String activeIngredient;
    
    @Column(length = 1000)
    private String indications;
    
    @Column(length = 1000)
    private String contraindications;
    
    @Column(length = 1000)
    private String knownAdverseEffects;
    
    @Column
    private String dosageForm;
    
    @Column
    private String strength;
    
    @Enumerated(EnumType.STRING)
    private DrugStatus status;
    
    @Column
    private LocalDateTime approvalDate;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdverseEvent> adverseEvents;
    
    public enum DrugStatus {
        ACTIVE, DISCONTINUED, SUSPENDED, UNDER_REVIEW
    }
}


