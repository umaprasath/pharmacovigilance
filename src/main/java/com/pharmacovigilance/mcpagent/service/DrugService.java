package com.pharmacovigilance.mcpagent.service;

import com.pharmacovigilance.mcpagent.model.Drug;
import com.pharmacovigilance.mcpagent.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DrugService {
    
    private final DrugRepository drugRepository;
    
    public Drug save(Drug drug) {
        log.info("Saving drug: {}", drug.getDrugName());
        return drugRepository.save(drug);
    }
    
    public Optional<Drug> findById(Long id) {
        return drugRepository.findById(id);
    }
    
    public Optional<Drug> findByDrugCode(String drugCode) {
        return drugRepository.findByDrugCode(drugCode);
    }
    
    public List<Drug> findAll() {
        return drugRepository.findAll();
    }
    
    public Page<Drug> findAll(Pageable pageable) {
        return drugRepository.findAll(pageable);
    }
    
    public List<Drug> findByNameContaining(String name) {
        return drugRepository.findByDrugNameContainingIgnoreCase(name);
    }
    
    public List<Drug> findByManufacturer(String manufacturer) {
        return drugRepository.findByManufacturerContainingIgnoreCase(manufacturer);
    }
    
    public long count() {
        return drugRepository.count();
    }
    
    public void deleteById(Long id) {
        drugRepository.deleteById(id);
    }
}


