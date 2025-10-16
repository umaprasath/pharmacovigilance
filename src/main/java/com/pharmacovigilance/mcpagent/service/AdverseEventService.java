package com.pharmacovigilance.mcpagent.service;

import com.pharmacovigilance.mcpagent.model.AdverseEvent;
import com.pharmacovigilance.mcpagent.repository.AdverseEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdverseEventService {
    
    private final AdverseEventRepository adverseEventRepository;
    
    public AdverseEvent save(AdverseEvent adverseEvent) {
        log.info("Saving adverse event: {}", adverseEvent.getCaseNumber());
        return adverseEventRepository.save(adverseEvent);
    }
    
    public Optional<AdverseEvent> findById(Long id) {
        return adverseEventRepository.findById(id);
    }
    
    public List<AdverseEvent> findAll() {
        return adverseEventRepository.findAll();
    }
    
    public Page<AdverseEvent> findAll(Pageable pageable) {
        return adverseEventRepository.findAll(pageable);
    }
    
    public List<AdverseEvent> findByCriteria(String severity, String status, String drugName, String patientId) {
        if (severity != null && status != null && drugName != null) {
            return adverseEventRepository.findBySeverityAndStatusAndDrugName(
                AdverseEvent.SeverityLevel.valueOf(severity),
                AdverseEvent.EventStatus.valueOf(status),
                drugName
            );
        } else if (severity != null && status != null) {
            return adverseEventRepository.findBySeverityAndStatus(
                AdverseEvent.SeverityLevel.valueOf(severity),
                AdverseEvent.EventStatus.valueOf(status)
            );
        } else if (severity != null) {
            return adverseEventRepository.findBySeverity(AdverseEvent.SeverityLevel.valueOf(severity));
        } else if (status != null) {
            return adverseEventRepository.findByStatus(AdverseEvent.EventStatus.valueOf(status));
        } else if (drugName != null) {
            return adverseEventRepository.findByDrugNameContainingIgnoreCase(drugName);
        } else if (patientId != null) {
            return adverseEventRepository.findByPatientPatientId(patientId);
        } else {
            return findAll();
        }
    }
    
    public Map<String, Long> getCountBySeverity() {
        return adverseEventRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                event -> event.getSeverity().name(),
                Collectors.counting()
            ));
    }
    
    public Map<String, Long> getCountByStatus() {
        return adverseEventRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                event -> event.getStatus().name(),
                Collectors.counting()
            ));
    }
    
    public long count() {
        return adverseEventRepository.count();
    }
    
    public void deleteById(Long id) {
        adverseEventRepository.deleteById(id);
    }
}
