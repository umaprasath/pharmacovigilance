package com.pharmacovigilance.mcpagent.repository;

import com.pharmacovigilance.mcpagent.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional<Patient> findByPatientId(String patientId);
    
    List<Patient> findByLastNameContainingIgnoreCase(String lastName);
    
    List<Patient> findByFirstNameContainingIgnoreCase(String firstName);
    
    List<Patient> findByGender(Patient.Gender gender);
}


