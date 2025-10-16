package com.pharmacovigilance.mcpagent.service;

import com.pharmacovigilance.mcpagent.model.AdverseEvent;
import com.pharmacovigilance.mcpagent.model.AiAnalysis;
import com.pharmacovigilance.mcpagent.repository.AiAnalysisRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AiAnalysisService {
    
    private final AiAnalysisRepository aiAnalysisRepository;
    private final OpenAiService openAiService;
    
    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    
    public AiAnalysis performCausalityAssessment(AdverseEvent adverseEvent) {
        log.info("Performing causality assessment for adverse event: {}", adverseEvent.getCaseNumber());
        
        String prompt = buildCausalityAssessmentPrompt(adverseEvent);
        String response = callOpenAI(prompt);
        
        AiAnalysis analysis = new AiAnalysis();
        analysis.setAdverseEvent(adverseEvent);
        analysis.setAnalysisPrompt(prompt);
        analysis.setAiResponse(response);
        analysis.setModelUsed(model);
        analysis.setAnalysisType(AiAnalysis.AnalysisType.CAUSALITY_ASSESSMENT);
        analysis.setStatus(AiAnalysis.AnalysisStatus.COMPLETED);
        
        // Extract insights and recommendations
        analysis.setExtractedInsights(extractInsights(response));
        analysis.setRecommendations(extractRecommendations(response));
        
        return aiAnalysisRepository.save(analysis);
    }
    
    public AiAnalysis performRiskAnalysis(AdverseEvent adverseEvent) {
        log.info("Performing risk analysis for adverse event: {}", adverseEvent.getCaseNumber());
        
        String prompt = buildRiskAnalysisPrompt(adverseEvent);
        String response = callOpenAI(prompt);
        
        AiAnalysis analysis = new AiAnalysis();
        analysis.setAdverseEvent(adverseEvent);
        analysis.setAnalysisPrompt(prompt);
        analysis.setAiResponse(response);
        analysis.setModelUsed(model);
        analysis.setAnalysisType(AiAnalysis.AnalysisType.RISK_ANALYSIS);
        analysis.setStatus(AiAnalysis.AnalysisStatus.COMPLETED);
        
        analysis.setExtractedInsights(extractInsights(response));
        analysis.setRecommendations(extractRecommendations(response));
        
        return aiAnalysisRepository.save(analysis);
    }
    
    public AiAnalysis performPatternDetection(List<AdverseEvent> adverseEvents) {
        log.info("Performing pattern detection for {} adverse events", adverseEvents.size());
        
        String prompt = buildPatternDetectionPrompt(adverseEvents);
        String response = callOpenAI(prompt);
        
        AiAnalysis analysis = new AiAnalysis();
        analysis.setAnalysisPrompt(prompt);
        analysis.setAiResponse(response);
        analysis.setModelUsed(model);
        analysis.setAnalysisType(AiAnalysis.AnalysisType.PATTERN_DETECTION);
        analysis.setStatus(AiAnalysis.AnalysisStatus.COMPLETED);
        
        analysis.setExtractedInsights(extractInsights(response));
        analysis.setRecommendations(extractRecommendations(response));
        
        return aiAnalysisRepository.save(analysis);
    }
    
    private String callOpenAI(String prompt) {
        try {
            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);
            
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(List.of(userMessage))
                .maxTokens(1000)
                .temperature(0.3)
                .build();
            
            return openAiService.createChatCompletion(completionRequest)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
            return "Error: Unable to perform AI analysis - " + e.getMessage();
        }
    }
    
    private String buildCausalityAssessmentPrompt(AdverseEvent adverseEvent) {
        return String.format("""
            As a pharmacovigilance expert, please assess the causality between the drug and adverse event.
            
            Case Details:
            - Case Number: %s
            - Drug: %s
            - Patient ID: %s
            - Adverse Event: %s
            - Severity: %s
            - Symptoms: %s
            - Medical History: %s
            - Concomitant Medications: %s
            
            Please provide:
            1. Causality assessment (Certain, Probable, Possible, Unlikely, Unclassifiable, Unassessable)
            2. Reasoning for your assessment
            3. Key factors that influenced your decision
            4. Recommendations for further investigation if needed
            
            Format your response as a structured analysis.
            """, 
            adverseEvent.getCaseNumber(),
            adverseEvent.getDrugName(),
            adverseEvent.getPatient() != null ? adverseEvent.getPatient().getPatientId() : "N/A",
            adverseEvent.getAdverseEventDescription(),
            adverseEvent.getSeverity(),
            adverseEvent.getSymptoms(),
            adverseEvent.getMedicalHistory(),
            adverseEvent.getConcomitantMedications()
        );
    }
    
    private String buildRiskAnalysisPrompt(AdverseEvent adverseEvent) {
        return String.format("""
            As a pharmacovigilance expert, please perform a risk analysis for this adverse event.
            
            Case Details:
            - Case Number: %s
            - Drug: %s
            - Patient ID: %s
            - Adverse Event: %s
            - Severity: %s
            - Symptoms: %s
            
            Please analyze:
            1. Risk level (Low, Medium, High, Critical)
            2. Potential impact on patient safety
            3. Regulatory implications
            4. Risk mitigation strategies
            5. Monitoring recommendations
            
            Format your response as a structured risk assessment.
            """,
            adverseEvent.getCaseNumber(),
            adverseEvent.getDrugName(),
            adverseEvent.getPatient() != null ? adverseEvent.getPatient().getPatientId() : "N/A",
            adverseEvent.getAdverseEventDescription(),
            adverseEvent.getSeverity(),
            adverseEvent.getSymptoms()
        );
    }
    
    private String buildPatternDetectionPrompt(List<AdverseEvent> adverseEvents) {
        StringBuilder prompt = new StringBuilder("""
            As a pharmacovigilance expert, please analyze the following adverse events for patterns and trends.
            
            Adverse Events Data:
            """);
        
        for (AdverseEvent event : adverseEvents) {
            prompt.append(String.format("""
                - Case: %s, Drug: %s, Event: %s, Severity: %s, Status: %s
                """, 
                event.getCaseNumber(),
                event.getDrugName(),
                event.getAdverseEventDescription(),
                event.getSeverity(),
                event.getStatus()
            ));
        }
        
        prompt.append("""
            
            Please identify:
            1. Common patterns across events
            2. Drug-specific trends
            3. Severity patterns
            4. Potential safety signals
            5. Recommendations for further investigation
            
            Format your response as a structured pattern analysis.
            """);
        
        return prompt.toString();
    }
    
    private String extractInsights(String response) {
        // Simple extraction - in a real implementation, you might use more sophisticated NLP
        if (response.contains("Key factors:")) {
            int start = response.indexOf("Key factors:");
            int end = response.indexOf("\n", start + 50);
            return end > start ? response.substring(start, end) : "Key insights extracted from analysis";
        }
        return "AI-generated insights from the analysis";
    }
    
    private String extractRecommendations(String response) {
        // Simple extraction - in a real implementation, you might use more sophisticated NLP
        if (response.contains("Recommendations:")) {
            int start = response.indexOf("Recommendations:");
            int end = response.indexOf("\n", start + 50);
            return end > start ? response.substring(start, end) : "AI-generated recommendations";
        }
        return "AI-generated recommendations from the analysis";
    }
    
    public List<AiAnalysis> findByAdverseEvent(AdverseEvent adverseEvent) {
        return aiAnalysisRepository.findByAdverseEvent(adverseEvent);
    }
    
    public Optional<AiAnalysis> findById(Long id) {
        return aiAnalysisRepository.findById(id);
    }
}
