package com.pharmacovigilance.mcpagent.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacovigilance.mcpagent.model.AdverseEvent;
import com.pharmacovigilance.mcpagent.model.AiAnalysis;
import com.pharmacovigilance.mcpagent.model.Drug;
import com.pharmacovigilance.mcpagent.model.Patient;
import com.pharmacovigilance.mcpagent.service.AdverseEventExtractionService;
import com.pharmacovigilance.mcpagent.service.AdverseEventService;
import com.pharmacovigilance.mcpagent.service.AiAnalysisService;
import com.pharmacovigilance.mcpagent.service.DocumentParsingService;
import com.pharmacovigilance.mcpagent.service.DrugService;
import com.pharmacovigilance.mcpagent.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class McpServer {
    
    private final AdverseEventService adverseEventService;
    private final PatientService patientService;
    private final DrugService drugService;
    private final AiAnalysisService aiAnalysisService;
    private final AdverseEventExtractionService extractionService;
    private final DocumentParsingService documentParsingService;
    private final ObjectMapper objectMapper;
    
    /**
     * MCP Tool: Get adverse events by criteria
     */
    public Map<String, Object> getAdverseEvents(Map<String, Object> params) {
        try {
            String severity = (String) params.get("severity");
            String status = (String) params.get("status");
            String drugName = (String) params.get("drugName");
            String patientId = (String) params.get("patientId");
            
            List<AdverseEvent> events = adverseEventService.findByCriteria(severity, status, drugName, patientId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", events);
            result.put("count", events.size());
            
            return result;
        } catch (Exception e) {
            log.error("Error getting adverse events", e);
            return createErrorResponse("Failed to retrieve adverse events: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Get patient information
     */
    public Map<String, Object> getPatientInfo(Map<String, Object> params) {
        try {
            String patientId = (String) params.get("patientId");
            if (patientId == null) {
                return createErrorResponse("Patient ID is required");
            }
            
            Optional<Patient> patient = patientService.findByPatientId(patientId);
            if (patient.isEmpty()) {
                return createErrorResponse("Patient not found");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", patient.get());
            
            return result;
        } catch (Exception e) {
            log.error("Error getting patient info", e);
            return createErrorResponse("Failed to retrieve patient information: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Get drug information
     */
    public Map<String, Object> getDrugInfo(Map<String, Object> params) {
        try {
            String drugCode = (String) params.get("drugCode");
            String drugName = (String) params.get("drugName");
            
            List<Drug> drugs;
            if (drugCode != null) {
                Optional<Drug> drug = drugService.findByDrugCode(drugCode);
                drugs = drug.map(List::of).orElse(List.of());
            } else if (drugName != null) {
                drugs = drugService.findByNameContaining(drugName);
            } else {
                return createErrorResponse("Either drugCode or drugName is required");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", drugs);
            result.put("count", drugs.size());
            
            return result;
        } catch (Exception e) {
            log.error("Error getting drug info", e);
            return createErrorResponse("Failed to retrieve drug information: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Create new adverse event
     */
    public Map<String, Object> createAdverseEvent(Map<String, Object> params) {
        try {
            AdverseEvent event = objectMapper.convertValue(params, AdverseEvent.class);
            AdverseEvent savedEvent = adverseEventService.save(event);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", savedEvent);
            result.put("message", "Adverse event created successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Error creating adverse event", e);
            return createErrorResponse("Failed to create adverse event: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Update adverse event status
     */
    public Map<String, Object> updateAdverseEventStatus(Map<String, Object> params) {
        try {
            Long eventId = Long.valueOf(params.get("eventId").toString());
            String status = (String) params.get("status");
            
            Optional<AdverseEvent> eventOpt = adverseEventService.findById(eventId);
            if (eventOpt.isEmpty()) {
                return createErrorResponse("Adverse event not found");
            }
            
            AdverseEvent event = eventOpt.get();
            event.setStatus(AdverseEvent.EventStatus.valueOf(status));
            AdverseEvent updatedEvent = adverseEventService.save(event);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", updatedEvent);
            result.put("message", "Adverse event status updated successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Error updating adverse event status", e);
            return createErrorResponse("Failed to update adverse event status: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Get statistics
     */
    public Map<String, Object> getStatistics(Map<String, Object> params) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get counts by severity
            Map<String, Long> severityCounts = adverseEventService.getCountBySeverity();
            stats.put("severityCounts", severityCounts);
            
            // Get counts by status
            Map<String, Long> statusCounts = adverseEventService.getCountByStatus();
            stats.put("statusCounts", statusCounts);
            
            // Get total counts
            stats.put("totalAdverseEvents", adverseEventService.count());
            stats.put("totalPatients", patientService.count());
            stats.put("totalDrugs", drugService.count());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", stats);
            
            return result;
        } catch (Exception e) {
            log.error("Error getting statistics", e);
            return createErrorResponse("Failed to retrieve statistics: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Classify an existing adverse event using AI
     */
    public Map<String, Object> classifyAdverseEvent(Map<String, Object> params) {
        try {
            Long eventId = Long.valueOf(params.get("eventId").toString());
            
            Optional<AdverseEvent> eventOpt = adverseEventService.findById(eventId);
            if (eventOpt.isEmpty()) {
                return createErrorResponse("Adverse event not found");
            }
            
            AdverseEvent event = eventOpt.get();
            
            // Perform AI classifications
            log.info("Performing AI classification for event: {}", event.getCaseNumber());
            
            AiAnalysis causalityAnalysis = aiAnalysisService.performCausalityAssessment(event);
            AiAnalysis riskAnalysis = aiAnalysisService.performRiskAnalysis(event);
            
            Map<String, Object> classification = new HashMap<>();
            classification.put("eventId", eventId);
            classification.put("caseNumber", event.getCaseNumber());
            classification.put("causalityAssessment", Map.of(
                "analysisId", causalityAnalysis.getId(),
                "type", causalityAnalysis.getAnalysisType(),
                "status", causalityAnalysis.getStatus(),
                "insights", causalityAnalysis.getExtractedInsights(),
                "recommendations", causalityAnalysis.getRecommendations(),
                "fullResponse", causalityAnalysis.getAiResponse()
            ));
            classification.put("riskAnalysis", Map.of(
                "analysisId", riskAnalysis.getId(),
                "type", riskAnalysis.getAnalysisType(),
                "status", riskAnalysis.getStatus(),
                "insights", riskAnalysis.getExtractedInsights(),
                "recommendations", riskAnalysis.getRecommendations(),
                "fullResponse", riskAnalysis.getAiResponse()
            ));
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", classification);
            result.put("message", "AI classification completed successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Error classifying adverse event", e);
            return createErrorResponse("Failed to classify adverse event: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Classify event from raw input data using AI
     * This analyzes the input without creating a database record
     */
    public Map<String, Object> classifyEventFromInput(Map<String, Object> params) {
        try {
            // Extract event details from input
            String caseNumber = (String) params.get("caseNumber");
            String drugName = (String) params.get("drugName");
            String adverseEventDescription = (String) params.get("adverseEventDescription");
            String severity = (String) params.get("severity");
            String symptoms = (String) params.get("symptoms");
            String medicalHistory = (String) params.get("medicalHistory");
            String concomitantMedications = (String) params.get("concomitantMedications");
            String patientId = (String) params.get("patientId");
            
            // Validate required fields
            if (drugName == null || adverseEventDescription == null) {
                return createErrorResponse("drugName and adverseEventDescription are required");
            }
            
            // Create a temporary AdverseEvent object for classification (not saved to DB)
            AdverseEvent tempEvent = new AdverseEvent();
            tempEvent.setCaseNumber(caseNumber != null ? caseNumber : "TEMP-" + System.currentTimeMillis());
            tempEvent.setDrugName(drugName);
            tempEvent.setAdverseEventDescription(adverseEventDescription);
            tempEvent.setSymptoms(symptoms);
            tempEvent.setMedicalHistory(medicalHistory);
            tempEvent.setConcomitantMedications(concomitantMedications);
            
            if (severity != null) {
                try {
                    tempEvent.setSeverity(AdverseEvent.SeverityLevel.valueOf(severity));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid severity level provided: {}", severity);
                }
            }
            
            // If patient ID is provided, try to fetch patient details
            if (patientId != null) {
                Optional<Patient> patient = patientService.findByPatientId(patientId);
                patient.ifPresent(tempEvent::setPatient);
            }
            
            log.info("Performing AI classification for input event: {}", tempEvent.getCaseNumber());
            
            // Perform AI classifications (these will be saved to DB)
            AiAnalysis causalityAnalysis = aiAnalysisService.performCausalityAssessment(tempEvent);
            AiAnalysis riskAnalysis = aiAnalysisService.performRiskAnalysis(tempEvent);
            
            Map<String, Object> classification = new HashMap<>();
            classification.put("inputData", Map.of(
                "drugName", drugName,
                "adverseEventDescription", adverseEventDescription,
                "severity", severity != null ? severity : "Not specified",
                "symptoms", symptoms != null ? symptoms : "Not provided"
            ));
            classification.put("causalityAssessment", Map.of(
                "analysisId", causalityAnalysis.getId(),
                "type", causalityAnalysis.getAnalysisType(),
                "status", causalityAnalysis.getStatus(),
                "insights", causalityAnalysis.getExtractedInsights(),
                "recommendations", causalityAnalysis.getRecommendations(),
                "fullResponse", causalityAnalysis.getAiResponse()
            ));
            classification.put("riskAnalysis", Map.of(
                "analysisId", riskAnalysis.getId(),
                "type", riskAnalysis.getAnalysisType(),
                "status", riskAnalysis.getStatus(),
                "insights", riskAnalysis.getExtractedInsights(),
                "recommendations", riskAnalysis.getRecommendations(),
                "fullResponse", riskAnalysis.getAiResponse()
            ));
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", classification);
            result.put("message", "AI classification from input completed successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Error classifying event from input", e);
            return createErrorResponse("Failed to classify event from input: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Classify adverse event from PDF document
     */
    public Map<String, Object> classifyFromPdf(Map<String, Object> params) {
        try {
            String base64Content = (String) params.get("pdfContent");
            String fileName = (String) params.getOrDefault("fileName", "document.pdf");
            
            if (base64Content == null) {
                return createErrorResponse("pdfContent (Base64 encoded) is required");
            }
            
            log.info("Processing PDF document: {}", fileName);
            
            // Step 1: Parse PDF to extract text
            String extractedText = documentParsingService.parsePdfFromBase64(base64Content);
            
            // Step 2: Extract structured adverse event data using AI
            Map<String, Object> extractedData = extractionService.extractFromClinicalDocument(extractedText);
            
            // Step 3: Validate and enrich data
            Map<String, Object> enrichedData = extractionService.validateAndEnrichExtractedData(extractedData);
            
            // Step 4: If valid, perform classification
            Map<String, Object> classification = null;
            if (Boolean.TRUE.equals(enrichedData.get("isValid"))) {
                classification = performClassificationOnExtractedData(enrichedData);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("fileName", fileName);
            result.put("extractedText", extractedText.substring(0, Math.min(500, extractedText.length())) + "...");
            result.put("extractedData", enrichedData);
            result.put("classification", classification);
            result.put("message", "PDF processed and adverse event classified successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Error classifying from PDF", e);
            return createErrorResponse("Failed to classify from PDF: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Classify adverse event from email
     */
    public Map<String, Object> classifyFromEmail(Map<String, Object> params) {
        try {
            String emailSubject = (String) params.get("subject");
            String emailBody = (String) params.get("body");
            String sender = (String) params.getOrDefault("sender", "Unknown");
            String base64Content = (String) params.get("emlContent");
            
            String subject, body, from;
            
            // Option 1: Parse from .eml file (Base64 encoded)
            if (base64Content != null) {
                log.info("Processing email from .eml file");
                Map<String, String> emailData = documentParsingService.parseEmailFromBase64(base64Content);
                subject = emailData.get("subject");
                body = emailData.get("body");
                from = emailData.get("from");
            }
            // Option 2: Direct email content
            else if (emailSubject != null && emailBody != null) {
                subject = emailSubject;
                body = emailBody;
                from = sender;
            } else {
                return createErrorResponse("Either emlContent or both subject and body are required");
            }
            
            log.info("Processing email from: {}, subject: {}", from, subject);
            
            // Extract structured adverse event data using AI
            Map<String, Object> extractedData = extractionService.extractFromEmail(subject, body, from);
            
            // Validate and enrich data
            Map<String, Object> enrichedData = extractionService.validateAndEnrichExtractedData(extractedData);
            
            // If valid, perform classification
            Map<String, Object> classification = null;
            if (Boolean.TRUE.equals(enrichedData.get("isValid"))) {
                classification = performClassificationOnExtractedData(enrichedData);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("emailMetadata", Map.of(
                "from", from,
                "subject", subject
            ));
            result.put("extractedData", enrichedData);
            result.put("classification", classification);
            result.put("message", "Email processed and adverse event classified successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Error classifying from email", e);
            return createErrorResponse("Failed to classify from email: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Classify adverse event from telephony transcript
     */
    public Map<String, Object> classifyFromTelephonyTranscript(Map<String, Object> params) {
        try {
            String transcript = (String) params.get("transcript");
            String callerInfo = (String) params.getOrDefault("callerInfo", "Unknown caller");
            String callId = (String) params.get("callId");
            
            if (transcript == null) {
                return createErrorResponse("transcript is required");
            }
            
            log.info("Processing telephony transcript for call: {}", callId);
            
            // Extract structured adverse event data using AI
            Map<String, Object> extractedData = extractionService.extractFromTelephonyTranscript(transcript, callerInfo);
            
            // Validate and enrich data
            Map<String, Object> enrichedData = extractionService.validateAndEnrichExtractedData(extractedData);
            
            // If valid, perform classification
            Map<String, Object> classification = null;
            if (Boolean.TRUE.equals(enrichedData.get("isValid"))) {
                classification = performClassificationOnExtractedData(enrichedData);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("callMetadata", Map.of(
                "callId", callId != null ? callId : "N/A",
                "callerInfo", callerInfo
            ));
            result.put("extractedData", enrichedData);
            result.put("classification", classification);
            result.put("message", "Telephony transcript processed and adverse event classified successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Error classifying from telephony transcript", e);
            return createErrorResponse("Failed to classify from telephony transcript: " + e.getMessage());
        }
    }
    
    /**
     * MCP Tool: Classify adverse event from any document
     */
    public Map<String, Object> classifyFromDocument(Map<String, Object> params) {
        try {
            String base64Content = (String) params.get("documentContent");
            String fileName = (String) params.getOrDefault("fileName", "document");
            String documentType = (String) params.getOrDefault("documentType", "auto");
            
            if (base64Content == null) {
                return createErrorResponse("documentContent (Base64 encoded) is required");
            }
            
            log.info("Processing document: {} (type: {})", fileName, documentType);
            
            // Parse document using Tika
            byte[] documentBytes = java.util.Base64.getDecoder().decode(base64Content);
            String extractedText = documentParsingService.parseDocument(documentBytes);
            
            // Extract structured adverse event data using AI
            Map<String, Object> extractedData = extractionService.extractFromClinicalDocument(extractedText);
            
            // Validate and enrich data
            Map<String, Object> enrichedData = extractionService.validateAndEnrichExtractedData(extractedData);
            
            // If valid, perform classification
            Map<String, Object> classification = null;
            if (Boolean.TRUE.equals(enrichedData.get("isValid"))) {
                classification = performClassificationOnExtractedData(enrichedData);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("fileName", fileName);
            result.put("extractedText", extractedText.substring(0, Math.min(500, extractedText.length())) + "...");
            result.put("extractedData", enrichedData);
            result.put("classification", classification);
            result.put("message", "Document processed and adverse event classified successfully");
            
            return result;
        } catch (Exception e) {
            log.error("Error classifying from document", e);
            return createErrorResponse("Failed to classify from document: " + e.getMessage());
        }
    }
    
    /**
     * Helper method: Perform AI classification on extracted data
     */
    private Map<String, Object> performClassificationOnExtractedData(Map<String, Object> extractedData) {
        try {
            // Create a temporary AdverseEvent object for classification
            AdverseEvent tempEvent = new AdverseEvent();
            tempEvent.setCaseNumber("EXTRACTED-" + System.currentTimeMillis());
            tempEvent.setDrugName((String) extractedData.get("drugName"));
            tempEvent.setAdverseEventDescription((String) extractedData.get("adverseEventDescription"));
            tempEvent.setSymptoms((String) extractedData.get("symptoms"));
            tempEvent.setMedicalHistory((String) extractedData.get("medicalHistory"));
            tempEvent.setConcomitantMedications((String) extractedData.get("concomitantMedications"));
            
            // Set severity if available
            String severity = (String) extractedData.get("severity");
            if (severity != null) {
                try {
                    tempEvent.setSeverity(AdverseEvent.SeverityLevel.valueOf(severity));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid severity level: {}", severity);
                }
            }
            
            // Perform AI classifications
            AiAnalysis causalityAnalysis = aiAnalysisService.performCausalityAssessment(tempEvent);
            AiAnalysis riskAnalysis = aiAnalysisService.performRiskAnalysis(tempEvent);
            
            return Map.of(
                "causalityAssessment", Map.of(
                    "analysisId", causalityAnalysis.getId(),
                    "type", causalityAnalysis.getAnalysisType(),
                    "status", causalityAnalysis.getStatus(),
                    "insights", causalityAnalysis.getExtractedInsights(),
                    "recommendations", causalityAnalysis.getRecommendations(),
                    "fullResponse", causalityAnalysis.getAiResponse()
                ),
                "riskAnalysis", Map.of(
                    "analysisId", riskAnalysis.getId(),
                    "type", riskAnalysis.getAnalysisType(),
                    "status", riskAnalysis.getStatus(),
                    "insights", riskAnalysis.getExtractedInsights(),
                    "recommendations", riskAnalysis.getRecommendations(),
                    "fullResponse", riskAnalysis.getAiResponse()
                )
            );
        } catch (Exception e) {
            log.error("Error performing classification on extracted data", e);
            return Map.of("error", "Classification failed: " + e.getMessage());
        }
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }
}


