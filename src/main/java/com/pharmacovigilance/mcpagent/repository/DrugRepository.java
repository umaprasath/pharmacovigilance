package com.pharmacovigilance.mcpagent.repository;

import com.pharmacovigilance.mcpagent.model.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {
    
    Optional<Drug> findByDrugCode(String drugCode);
    
    List<Drug> findByDrugNameContainingIgnoreCase(String drugName);
    
    List<Drug> findByManufacturerContainingIgnoreCase(String manufacturer);
    
    List<Drug> findByStatus(Drug.DrugStatus status);
    
    List<Drug> findByActiveIngredientContainingIgnoreCase(String activeIngredient);
}


