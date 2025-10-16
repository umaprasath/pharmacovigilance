package com.pharmacovigilance.mcpagent.service;

import com.pharmacovigilance.mcpagent.model.Patient;
import com.pharmacovigilance.mcpagent.repository.PatientRepository;
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
public class PatientService {
    
    private final PatientRepository patientRepository;
    
    public Patient save(Patient patient) {
        log.info("Saving patient: {}", patient.getPatientId());
        return patientRepository.save(patient);
    }
    
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }
    
    public Optional<Patient> findByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId);
    }
    
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }
    
    public Page<Patient> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }
    
    public List<Patient> findByLastNameContaining(String lastName) {
        return patientRepository.findByLastNameContainingIgnoreCase(lastName);
    }
    
    public long count() {
        return patientRepository.count();
    }
    
    public void deleteById(Long id) {
        patientRepository.deleteById(id);
    }
}


