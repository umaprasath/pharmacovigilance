package com.pharmacovigilance.mcpagent.config;

import com.pharmacovigilance.mcpagent.model.AdverseEvent;
import com.pharmacovigilance.mcpagent.model.Drug;
import com.pharmacovigilance.mcpagent.model.Patient;
import com.pharmacovigilance.mcpagent.service.AdverseEventService;
import com.pharmacovigilance.mcpagent.service.DrugService;
import com.pharmacovigilance.mcpagent.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataInitializer implements CommandLineRunner {
    
    private final PatientService patientService;
    private final DrugService drugService;
    private final AdverseEventService adverseEventService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");
        
        // Create sample patients
        createSamplePatients();
        
        // Create sample drugs
        createSampleDrugs();
        
        // Create sample adverse events
        createSampleAdverseEvents();
        
        log.info("Sample data initialization completed");
    }
    
    private void createSamplePatients() {
        Patient patient1 = new Patient();
        patient1.setPatientId("P001");
        patient1.setFirstName("John");
        patient1.setLastName("Doe");
        patient1.setDateOfBirth(LocalDate.of(1980, 5, 15));
        patient1.setGender(Patient.Gender.MALE);
        patient1.setWeight(75.5);
        patient1.setHeight(175.0);
        patient1.setMedicalHistory("Hypertension, Diabetes Type 2");
        patient1.setAllergies("Penicillin");
        patient1.setCurrentMedications("Metformin, Lisinopril");
        patient1.setContactInfo("john.doe@email.com");
        patientService.save(patient1);
        
        Patient patient2 = new Patient();
        patient2.setPatientId("P002");
        patient2.setFirstName("Jane");
        patient2.setLastName("Smith");
        patient2.setDateOfBirth(LocalDate.of(1975, 8, 22));
        patient2.setGender(Patient.Gender.FEMALE);
        patient2.setWeight(65.0);
        patient2.setHeight(165.0);
        patient2.setMedicalHistory("Asthma, Migraine");
        patient2.setAllergies("None known");
        patient2.setCurrentMedications("Albuterol, Sumatriptan");
        patient2.setContactInfo("jane.smith@email.com");
        patientService.save(patient2);
        
        log.info("Created 2 sample patients");
    }
    
    private void createSampleDrugs() {
        Drug drug1 = new Drug();
        drug1.setDrugCode("ASP001");
        drug1.setDrugName("Aspirin");
        drug1.setGenericName("Acetylsalicylic Acid");
        drug1.setManufacturer("Bayer");
        drug1.setActiveIngredient("Acetylsalicylic Acid");
        drug1.setIndications("Pain relief, Anti-inflammatory, Anti-platelet");
        drug1.setContraindications("Active bleeding, Peptic ulcer, Severe liver disease");
        drug1.setKnownAdverseEffects("Gastrointestinal bleeding, Nausea, Vomiting, Headache");
        drug1.setDosageForm("Tablet");
        drug1.setStrength("325mg");
        drug1.setStatus(Drug.DrugStatus.ACTIVE);
        drug1.setApprovalDate(LocalDateTime.now().minusYears(10));
        drugService.save(drug1);
        
        Drug drug2 = new Drug();
        drug2.setDrugCode("MET001");
        drug2.setDrugName("Metformin");
        drug2.setGenericName("Metformin Hydrochloride");
        drug2.setManufacturer("Generic Pharma");
        drug2.setActiveIngredient("Metformin Hydrochloride");
        drug2.setIndications("Type 2 Diabetes Mellitus");
        drug2.setContraindications("Severe renal impairment, Metabolic acidosis");
        drug2.setKnownAdverseEffects("Nausea, Diarrhea, Abdominal pain, Metallic taste");
        drug2.setDosageForm("Tablet");
        drug2.setStrength("500mg");
        drug2.setStatus(Drug.DrugStatus.ACTIVE);
        drug2.setApprovalDate(LocalDateTime.now().minusYears(5));
        drugService.save(drug2);
        
        log.info("Created 2 sample drugs");
    }
    
    private void createSampleAdverseEvents() {
        // Retrieve patients from database
        Patient patient1 = patientService.findByPatientId("P001").orElse(null);
        Patient patient2 = patientService.findByPatientId("P002").orElse(null);
        
        AdverseEvent event1 = new AdverseEvent();
        event1.setCaseNumber("AE-2024-001");
        event1.setPatient(patient1);
        event1.setDrugName("Aspirin");
        event1.setAdverseEventDescription("Severe headache and nausea after taking aspirin");
        event1.setSeverity(AdverseEvent.SeverityLevel.MODERATE);
        event1.setStatus(AdverseEvent.EventStatus.NEW);
        event1.setSymptoms("Severe headache, nausea, dizziness");
        event1.setMedicalHistory("Hypertension, Diabetes Type 2");
        event1.setConcomitantMedications("Metformin, Lisinopril");
        event1.setReporterNotes("Patient reported symptoms 2 hours after taking aspirin");
        event1.setEventDate(LocalDateTime.now().minusDays(1));
        event1.setReportDate(LocalDateTime.now());
        adverseEventService.save(event1);
        
        AdverseEvent event2 = new AdverseEvent();
        event2.setCaseNumber("AE-2024-002");
        event2.setPatient(patient2);
        event2.setDrugName("Metformin");
        event2.setAdverseEventDescription("Gastrointestinal upset and metallic taste");
        event2.setSeverity(AdverseEvent.SeverityLevel.MILD);
        event2.setStatus(AdverseEvent.EventStatus.NEW);
        event2.setSymptoms("Nausea, diarrhea, metallic taste in mouth");
        event2.setMedicalHistory("Asthma, Migraine");
        event2.setConcomitantMedications("Albuterol, Sumatriptan");
        event2.setReporterNotes("Patient started experiencing symptoms after 3 days of treatment");
        event2.setEventDate(LocalDateTime.now().minusDays(3));
        event2.setReportDate(LocalDateTime.now());
        adverseEventService.save(event2);
        
        log.info("Created 2 sample adverse events");
    }
}
