package com.pharmacovigilance.mcpagent.controller;

import com.pharmacovigilance.mcpagent.model.Drug;
import com.pharmacovigilance.mcpagent.service.DrugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Drugs", description = "API endpoints for managing drug information")
public class DrugController {
    
    private final DrugService drugService;
    
    @GetMapping
    public ResponseEntity<Page<Drug>> getAllDrugs(Pageable pageable) {
        log.info("Fetching all drugs with pagination");
        Page<Drug> drugs = drugService.findAll(pageable);
        return ResponseEntity.ok(drugs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Drug> getDrugById(@PathVariable Long id) {
        log.info("Fetching drug with id: {}", id);
        Optional<Drug> drug = drugService.findById(id);
        return drug.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/drug-code/{drugCode}")
    public ResponseEntity<Drug> getDrugByDrugCode(@PathVariable String drugCode) {
        log.info("Fetching drug with drugCode: {}", drugCode);
        Optional<Drug> drug = drugService.findByDrugCode(drugCode);
        return drug.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Drug> createDrug(@Valid @RequestBody Drug drug) {
        log.info("Creating new drug: {}", drug.getDrugName());
        Drug savedDrug = drugService.save(drug);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDrug);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Drug> updateDrug(@PathVariable Long id, 
                                         @Valid @RequestBody Drug drug) {
        log.info("Updating drug with id: {}", id);
        Optional<Drug> existingDrug = drugService.findById(id);
        
        if (existingDrug.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        drug.setId(id);
        Drug updatedDrug = drugService.save(drug);
        return ResponseEntity.ok(updatedDrug);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrug(@PathVariable Long id) {
        log.info("Deleting drug with id: {}", id);
        Optional<Drug> drug = drugService.findById(id);
        
        if (drug.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        drugService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Drug>> searchDrugs(@RequestParam String name) {
        log.info("Searching drugs with name: {}", name);
        List<Drug> drugs = drugService.findByNameContaining(name);
        return ResponseEntity.ok(drugs);
    }
    
    @GetMapping("/manufacturer/{manufacturer}")
    public ResponseEntity<List<Drug>> getDrugsByManufacturer(@PathVariable String manufacturer) {
        log.info("Fetching drugs by manufacturer: {}", manufacturer);
        List<Drug> drugs = drugService.findByManufacturer(manufacturer);
        return ResponseEntity.ok(drugs);
    }
}
