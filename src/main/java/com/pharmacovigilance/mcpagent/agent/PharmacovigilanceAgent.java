package com.pharmacovigilance.mcpagent.agent;

import com.pharmacovigilance.mcpagent.model.AdverseEvent;
import com.pharmacovigilance.mcpagent.model.FollowUpAction;
import com.pharmacovigilance.mcpagent.service.AdverseEventService;
import com.pharmacovigilance.mcpagent.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PharmacovigilanceAgent {
    
    private final AdverseEventService adverseEventService;
    private final AiAnalysisService aiAnalysisService;
    
    /**
     * Automated workflow triggered when a new adverse event is created
     */
    @Async
    public CompletableFuture<Void> processNewAdverseEvent(Long adverseEventId) {
        log.info("Starting automated processing for adverse event: {}", adverseEventId);
        
        try {
            AdverseEvent adverseEvent = adverseEventService.findById(adverseEventId)
                .orElseThrow(() -> new RuntimeException("Adverse event not found"));
            
            // Step 1: Perform AI analysis
            log.info("Step 1: Performing AI analysis for case: {}", adverseEvent.getCaseNumber());
            aiAnalysisService.performCausalityAssessment(adverseEvent);
            aiAnalysisService.performRiskAnalysis(adverseEvent);
            
            // Step 2: Determine follow-up actions based on severity and analysis
            log.info("Step 2: Determining follow-up actions for case: {}", adverseEvent.getCaseNumber());
            determineFollowUpActions(adverseEvent);
            
            // Step 3: Update event status based on analysis
            log.info("Step 3: Updating event status for case: {}", adverseEvent.getCaseNumber());
            updateEventStatus(adverseEvent);
            
            log.info("Completed automated processing for adverse event: {}", adverseEventId);
            
        } catch (Exception e) {
            log.error("Error in automated processing for adverse event: {}", adverseEventId, e);
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Scheduled task to process pending adverse events
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void processPendingEvents() {
        log.info("Running scheduled processing for pending adverse events");
        
        try {
            List<AdverseEvent> pendingEvents = adverseEventService.findByCriteria(
                null, "NEW", null, null
            );
            
            for (AdverseEvent event : pendingEvents) {
                if (event.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
                    processNewAdverseEvent(event.getId());
                }
            }
            
        } catch (Exception e) {
            log.error("Error in scheduled processing", e);
        }
    }
    
    /**
     * Scheduled task to perform pattern detection analysis
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void performDailyPatternAnalysis() {
        log.info("Running daily pattern analysis");
        
        try {
            List<AdverseEvent> recentEvents = adverseEventService.findByCriteria(
                null, null, null, null
            );
            
            if (recentEvents.size() >= 5) {
                aiAnalysisService.performPatternDetection(recentEvents);
                log.info("Completed daily pattern analysis for {} events", recentEvents.size());
            }
            
        } catch (Exception e) {
            log.error("Error in daily pattern analysis", e);
        }
    }
    
    private void determineFollowUpActions(AdverseEvent adverseEvent) {
        // Create follow-up actions based on severity and analysis
        if (adverseEvent.getSeverity() == AdverseEvent.SeverityLevel.SEVERE || 
            adverseEvent.getSeverity() == AdverseEvent.SeverityLevel.LIFE_THREATENING) {
            
            createFollowUpAction(adverseEvent, 
                FollowUpAction.ActionType.INVESTIGATION, 
                "Immediate investigation required due to severe adverse event",
                "Safety Team");
            
            createFollowUpAction(adverseEvent, 
                FollowUpAction.ActionType.REGULATORY_SUBMISSION, 
                "Prepare regulatory submission for severe adverse event",
                "Regulatory Team");
        }
        
        if (adverseEvent.getSeverity() == AdverseEvent.SeverityLevel.MODERATE) {
            createFollowUpAction(adverseEvent, 
                FollowUpAction.ActionType.PATIENT_FOLLOW_UP, 
                "Schedule patient follow-up for moderate adverse event",
                "Clinical Team");
        }
        
        // Always create a general review action
        createFollowUpAction(adverseEvent, 
            FollowUpAction.ActionType.DRUG_SAFETY_REVIEW, 
            "Review drug safety profile based on new adverse event",
            "Drug Safety Team");
    }
    
    private void createFollowUpAction(AdverseEvent adverseEvent, 
                                   FollowUpAction.ActionType actionType, 
                                   String description, 
                                   String assignedTo) {
        FollowUpAction action = new FollowUpAction();
        action.setAdverseEvent(adverseEvent);
        action.setActionType(actionType);
        action.setDescription(description);
        action.setStatus(FollowUpAction.ActionStatus.PENDING);
        action.setAssignedTo(assignedTo);
        action.setDueDate(LocalDateTime.now().plusDays(7)); // Due in 7 days
        
        // In a real implementation, you would save this to the database
        log.info("Created follow-up action: {} for case: {}", actionType, adverseEvent.getCaseNumber());
    }
    
    private void updateEventStatus(AdverseEvent adverseEvent) {
        // Update status based on severity and analysis results
        if (adverseEvent.getSeverity() == AdverseEvent.SeverityLevel.SEVERE || 
            adverseEvent.getSeverity() == AdverseEvent.SeverityLevel.LIFE_THREATENING) {
            adverseEvent.setStatus(AdverseEvent.EventStatus.UNDER_INVESTIGATION);
        } else {
            adverseEvent.setStatus(AdverseEvent.EventStatus.CONFIRMED);
        }
        
        adverseEventService.save(adverseEvent);
        log.info("Updated status for case: {} to {}", 
            adverseEvent.getCaseNumber(), adverseEvent.getStatus());
    }
}


