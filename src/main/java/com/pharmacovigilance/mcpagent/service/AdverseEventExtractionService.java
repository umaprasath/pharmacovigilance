package com.pharmacovigilance.mcpagent.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdverseEventExtractionService {
    
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;
    
    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    
    /**
     * Extract structured adverse event data from unstructured text using OpenAI
     */
    public Map<String, Object> extractAdverseEventFromText(String text, String sourceType) {
        log.info("Extracting adverse event from {} with text length: {}", sourceType, text.length());
        
        String prompt = buildExtractionPrompt(text, sourceType);
        String response = callOpenAI(prompt);
        
        // Parse the AI response to extract structured data
        return parseAiResponse(response);
    }
    
    /**
     * Extract adverse event from clinical document (PDF content)
     */
    public Map<String, Object> extractFromClinicalDocument(String documentText) {
        return extractAdverseEventFromText(documentText, "clinical_document");
    }
    
    /**
     * Extract adverse event from email content
     */
    public Map<String, Object> extractFromEmail(String emailSubject, String emailBody, String sender) {
        String fullText = String.format("""
            Email From: %s
            Subject: %s
            
            Body:
            %s
            """, sender, emailSubject, emailBody);
        
        return extractAdverseEventFromText(fullText, "email");
    }
    
    /**
     * Extract adverse event from telephony transcript
     */
    public Map<String, Object> extractFromTelephonyTranscript(String transcript, String callerInfo) {
        String fullText = String.format("""
            Call From: %s
            
            Transcript:
            %s
            """, callerInfo, transcript);
        
        return extractAdverseEventFromText(fullText, "telephony_transcript");
    }
    
    private String buildExtractionPrompt(String text, String sourceType) {
        return String.format("""
            You are a pharmacovigilance expert. Extract adverse event information from the following %s.
            
            Text Content:
            %s
            
            Extract the following information in JSON format:
            {
              "drugName": "Name of the drug/medication involved",
              "adverseEventDescription": "Description of the adverse event",
              "severity": "MILD, MODERATE, SEVERE, LIFE_THREATENING, or FATAL",
              "symptoms": "List of symptoms observed",
              "patientAge": "Patient age if mentioned",
              "patientGender": "MALE, FEMALE, or OTHER",
              "medicalHistory": "Relevant medical history",
              "concomitantMedications": "Other medications being taken",
              "dateOfOnset": "When the adverse event started",
              "outcome": "Current outcome or status",
              "reporterName": "Name of person reporting",
              "reporterType": "PHYSICIAN, NURSE, PHARMACIST, PATIENT, or OTHER",
              "additionalNotes": "Any other relevant information"
            }
            
            Important instructions:
            - Extract only information that is explicitly mentioned in the text
            - Use "Not mentioned" for fields where information is not available
            - For severity, make your best assessment based on the description
            - Ensure the response is valid JSON
            - Be precise and factual
            
            Return ONLY the JSON object, no additional text or explanation.
            """, sourceType, text);
    }
    
    private String callOpenAI(String prompt) {
        try {
            ChatMessage systemMessage = new ChatMessage(
                ChatMessageRole.SYSTEM.value(),
                "You are a pharmacovigilance expert specialized in extracting structured adverse event data from unstructured text. Always respond with valid JSON."
            );
            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
            
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(systemMessage, userMessage))
                .maxTokens(1500)
                .temperature(0.2) // Lower temperature for more consistent extraction
                .build();
            
            return openAiService.createChatCompletion(completionRequest)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
            throw new RuntimeException("Failed to extract adverse event data: " + e.getMessage());
        }
    }
    
    private Map<String, Object> parseAiResponse(String response) {
        try {
            // Clean the response - remove markdown code blocks if present
            String cleanedResponse = response.trim();
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7);
            }
            if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3);
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();
            
            // Parse the JSON response
            Map<String, Object> extractedData = objectMapper.readValue(
                cleanedResponse, 
                new TypeReference<Map<String, Object>>() {}
            );
            
            // Clean up "Not mentioned" values to null for better handling
            Map<String, Object> cleanedData = new HashMap<>();
            extractedData.forEach((key, value) -> {
                if (value instanceof String && 
                    ("Not mentioned".equalsIgnoreCase((String) value) || 
                     "Not available".equalsIgnoreCase((String) value) ||
                     "N/A".equalsIgnoreCase((String) value))) {
                    cleanedData.put(key, null);
                } else {
                    cleanedData.put(key, value);
                }
            });
            
            log.info("Successfully extracted adverse event data with {} fields", cleanedData.size());
            return cleanedData;
            
        } catch (Exception e) {
            log.error("Error parsing AI response: {}", response, e);
            // Return partial data with error information
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("extractionError", "Failed to parse AI response: " + e.getMessage());
            errorData.put("rawResponse", response);
            return errorData;
        }
    }
    
    /**
     * Validate and enrich extracted data
     */
    public Map<String, Object> validateAndEnrichExtractedData(Map<String, Object> extractedData) {
        Map<String, Object> enrichedData = new HashMap<>(extractedData);
        
        // Add extraction metadata
        enrichedData.put("extractionTimestamp", System.currentTimeMillis());
        enrichedData.put("extractionSource", "OpenAI-" + model);
        
        // Validate required fields
        boolean hasRequiredFields = extractedData.containsKey("drugName") && 
                                   extractedData.get("drugName") != null &&
                                   extractedData.containsKey("adverseEventDescription") &&
                                   extractedData.get("adverseEventDescription") != null;
        
        enrichedData.put("isValid", hasRequiredFields);
        
        if (!hasRequiredFields) {
            enrichedData.put("validationError", "Missing required fields: drugName and adverseEventDescription");
        }
        
        return enrichedData;
    }
}

