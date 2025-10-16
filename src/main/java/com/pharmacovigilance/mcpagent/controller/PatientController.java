package com.pharmacovigilance.mcpagent.controller;

import com.pharmacovigilance.mcpagent.model.Patient;
import com.pharmacovigilance.mcpagent.service.PatientService;
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
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patients", description = "API endpoints for managing patient information")
public class PatientController {
    
    private final PatientService patientService;
    
    @GetMapping
    public ResponseEntity<Page<Patient>> getAllPatients(Pageable pageable) {
        log.info("Fetching all patients with pagination");
        Page<Patient> patients = patientService.findAll(pageable);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        log.info("Fetching patient with id: {}", id);
        Optional<Patient> patient = patientService.findById(id);
        return patient.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/patient-id/{patientId}")
    public ResponseEntity<Patient> getPatientByPatientId(@PathVariable String patientId) {
        log.info("Fetching patient with patientId: {}", patientId);
        Optional<Patient> patient = patientService.findByPatientId(patientId);
        return patient.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody Patient patient) {
        log.info("Creating new patient: {}", patient.getPatientId());
        Patient savedPatient = patientService.save(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, 
                                               @Valid @RequestBody Patient patient) {
        log.info("Updating patient with id: {}", id);
        Optional<Patient> existingPatient = patientService.findById(id);
        
        if (existingPatient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        patient.setId(id);
        Patient updatedPatient = patientService.save(patient);
        return ResponseEntity.ok(updatedPatient);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        log.info("Deleting patient with id: {}", id);
        Optional<Patient> patient = patientService.findById(id);
        
        if (patient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        patientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String lastName) {
        log.info("Searching patients with last name: {}", lastName);
        List<Patient> patients = patientService.findByLastNameContaining(lastName);
        return ResponseEntity.ok(patients);
    }
}
