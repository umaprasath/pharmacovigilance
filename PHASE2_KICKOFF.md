# Phase 2 Kickoff Guide - Workflow API + Automation

**Duration:** 3 weeks (Weeks 5-7)  
**Status:** 🟡 READY TO START  
**Previous Phase:** ✅ Phase 1 - MCP + Email Processing (COMPLETED)

---

## 🎯 Phase 2 Objectives

Build an automated workflow system that:
1. **Orchestrates** adverse event processing from creation to closure
2. **Automates** AI analysis and follow-up action generation
3. **Schedules** recurring tasks for pattern detection
4. **Notifies** stakeholders via email
5. **Exposes** workflow operations via MCP tools and REST API

---

## 📋 Week-by-Week Plan

### Week 5: Workflow Engine Core

**Goal:** Build the workflow state machine and core services

#### Day 1-2: Design & Schema
- [ ] Design workflow state machine diagram
- [ ] Define state transition rules
- [ ] Create database schema for workflow tracking
- [ ] Design FollowUpAction enhancements

#### Day 3-4: Implementation
- [ ] Create `WorkflowService.java`
- [ ] Implement state machine logic
- [ ] Add state transition validation
- [ ] Create `WorkflowHistory` entity

#### Day 5: Testing
- [ ] Unit tests for state transitions
- [ ] Integration tests
- [ ] Code review

**Deliverables:**
- [ ] WorkflowService with state machine
- [ ] Database migrations
- [ ] Unit tests (>80% coverage)

---

### Week 6: Automated Processing & Scheduling

**Goal:** Implement automated event processing and scheduled tasks

#### Day 1-2: Async Processing
- [ ] Enhance `PharmacovigilanceAgent.java`
- [ ] Implement `processNewAdverseEvent()` with workflow integration
- [ ] Add CompletableFuture for async processing
- [ ] Configure thread pool

#### Day 3-4: Scheduled Tasks
- [ ] Scheduled pending event processor (every 5 min)
- [ ] Scheduled pattern analysis (daily at 2 AM)
- [ ] Follow-up action generation
- [ ] Email notification service

#### Day 5: Integration
- [ ] Connect email classification to workflow
- [ ] Test end-to-end automation
- [ ] Performance testing

**Deliverables:**
- [ ] Automated processing working
- [ ] Scheduled tasks configured
- [ ] Email notification service
- [ ] Integration tests

---

### Week 7: Workflow API & MCP Tools

**Goal:** Expose workflow functionality via API and MCP

#### Day 1-2: REST API
- [ ] Create `WorkflowController.java`
- [ ] Implement workflow endpoints (8 endpoints)
- [ ] Add validation and error handling
- [ ] Swagger documentation

#### Day 3-4: MCP Integration
- [ ] Add 4 new MCP tools to `McpServer.java`
- [ ] Update `McpController.java`
- [ ] Tool documentation
- [ ] Testing

#### Day 5: Documentation & Demo
- [ ] API documentation
- [ ] Usage examples
- [ ] Demo preparation
- [ ] Phase 2 review

**Deliverables:**
- [ ] Workflow REST API (8 endpoints)
- [ ] 4 new MCP tools
- [ ] Complete documentation
- [ ] Demo-ready system

---

## 🏗️ Technical Architecture

### Workflow State Machine

```java
public enum WorkflowState {
    NEW,              // Initial state
    UNDER_REVIEW,     // Being reviewed by safety officer
    INVESTIGATION,    // Detailed investigation required
    CONFIRMED,        // Event confirmed
    REJECTED,         // Event rejected
    CLOSED            // Case closed
}

// Valid Transitions
NEW → UNDER_REVIEW → INVESTIGATION → CONFIRMED → CLOSED
      ↓
    REJECTED
```

### Automated Processing Flow

```
Event Created (via classify_from_email)
    ↓
[Async] processNewAdverseEvent()
    ↓
    ├─→ AI Causality Assessment (3-5 sec)
    ├─→ AI Risk Analysis (3-5 sec)
    ├─→ Determine Follow-up Actions
    │   ├─→ If SEVERE: Investigation + Regulatory Submission
    │   ├─→ If MODERATE: Patient Follow-up
    │   └─→ Always: Drug Safety Review
    └─→ Update Event Status
        ├─→ If SEVERE/LIFE_THREATENING: UNDER_INVESTIGATION
        └─→ Else: CONFIRMED
    ↓
Send Email Notification
    ├─→ To: Safety Team
    ├─→ CC: Reporter
    └─→ Subject: "Adverse Event #{caseNumber} - {status}"
```

---

## 📝 Implementation Details

### 1. WorkflowService.java

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {
    
    private final AdverseEventRepository adverseEventRepository;
    private final WorkflowHistoryRepository workflowHistoryRepository;
    
    /**
     * Start a workflow for an adverse event
     */
    public Workflow startWorkflow(Long eventId, String assignee) {
        // Implementation
    }
    
    /**
     * Transition workflow to a new state
     */
    public Workflow transitionState(Long workflowId, WorkflowState newState, 
                                    String reason, String userId) {
        // Validate transition
        // Update state
        // Record history
        // Send notifications
    }
    
    /**
     * Validate if a state transition is allowed
     */
    private boolean isValidTransition(WorkflowState current, WorkflowState next) {
        // Implement state machine rules
    }
    
    /**
     * Get workflow status
     */
    public WorkflowStatus getWorkflowStatus(Long workflowId) {
        // Implementation
    }
}
```

### 2. Enhanced PharmacovigilanceAgent.java

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class PharmacovigilanceAgent {
    
    private final AdverseEventService adverseEventService;
    private final AiAnalysisService aiAnalysisService;
    private final WorkflowService workflowService;
    private final EmailNotificationService emailService;
    
    /**
     * Enhanced automated workflow
     */
    @Async
    public CompletableFuture<Void> processNewAdverseEvent(Long adverseEventId) {
        log.info("Starting automated processing for event: {}", adverseEventId);
        
        try {
            AdverseEvent event = adverseEventService.findById(adverseEventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
            
            // Start workflow
            Workflow workflow = workflowService.startWorkflow(adverseEventId, "safety_team");
            
            // Step 1: AI Analysis
            log.info("Performing AI analysis...");
            AiAnalysis causality = aiAnalysisService.performCausalityAssessment(event);
            AiAnalysis risk = aiAnalysisService.performRiskAnalysis(event);
            
            // Step 2: Generate follow-up actions
            log.info("Generating follow-up actions...");
            List<FollowUpAction> actions = determineFollowUpActions(event);
            
            // Step 3: Update workflow state based on severity
            log.info("Updating workflow state...");
            WorkflowState newState = determineWorkflowState(event);
            workflowService.transitionState(workflow.getId(), newState, 
                "Automated processing completed", "SYSTEM");
            
            // Step 4: Send notifications
            log.info("Sending notifications...");
            emailService.sendEventProcessedNotification(event, workflow);
            
            log.info("Completed automated processing for event: {}", adverseEventId);
            
        } catch (Exception e) {
            log.error("Error in automated processing", e);
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Scheduled task: Process pending events
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void processPendingEvents() {
        log.info("Running scheduled processing for pending events");
        
        List<AdverseEvent> pendingEvents = adverseEventService
            .findByCriteria(null, "NEW", null, null);
        
        for (AdverseEvent event : pendingEvents) {
            if (event.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
                processNewAdverseEvent(event.getId());
            }
        }
    }
    
    /**
     * Scheduled task: Daily pattern analysis
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void performDailyPatternAnalysis() {
        log.info("Running daily pattern analysis");
        
        List<AdverseEvent> recentEvents = adverseEventService
            .findEventsInLastDays(30);
        
        if (recentEvents.size() >= 5) {
            aiAnalysisService.performPatternDetection(recentEvents);
        }
    }
}
```

### 3. New Entities

#### Workflow.java
```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "adverse_event_id")
    private AdverseEvent adverseEvent;
    
    @Enumerated(EnumType.STRING)
    private WorkflowState currentState;
    
    private String assignedTo;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

#### WorkflowHistory.java
```java
@Entity
@Data
@NoArgsConstructor
public class WorkflowHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;
    
    @Enumerated(EnumType.STRING)
    private WorkflowState fromState;
    
    @Enumerated(EnumType.STRING)
    private WorkflowState toState;
    
    private String transitionReason;
    private String userId;
    
    @CreatedDate
    private LocalDateTime timestamp;
}
```

### 4. New API Endpoints

#### WorkflowController.java
```java
@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Workflow Management")
public class WorkflowController {
    
    private final WorkflowService workflowService;
    
    @PostMapping("/start")
    public ResponseEntity<Workflow> startWorkflow(@RequestBody StartWorkflowRequest request) {
        // Implementation
    }
    
    @PostMapping("/{id}/transition")
    public ResponseEntity<Workflow> transitionState(
            @PathVariable Long id,
            @RequestBody TransitionRequest request) {
        // Implementation
    }
    
    @GetMapping("/{id}/status")
    public ResponseEntity<WorkflowStatus> getStatus(@PathVariable Long id) {
        // Implementation
    }
    
    @GetMapping("/{id}/history")
    public ResponseEntity<List<WorkflowHistory>> getHistory(@PathVariable Long id) {
        // Implementation
    }
    
    @PostMapping("/{id}/assign")
    public ResponseEntity<Workflow> assignWorkflow(
            @PathVariable Long id,
            @RequestBody AssignRequest request) {
        // Implementation
    }
}
```

### 5. New MCP Tools

Add to `McpServer.java`:

```java
/**
 * MCP Tool: Start workflow for an adverse event
 */
public Map<String, Object> startWorkflow(Map<String, Object> params) {
    try {
        Long eventId = Long.valueOf(params.get("eventId").toString());
        String assignee = (String) params.getOrDefault("assignee", "safety_team");
        
        Workflow workflow = workflowService.startWorkflow(eventId, assignee);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", workflow);
        result.put("message", "Workflow started successfully");
        
        return result;
    } catch (Exception e) {
        log.error("Error starting workflow", e);
        return createErrorResponse("Failed to start workflow: " + e.getMessage());
    }
}

/**
 * MCP Tool: Get workflow status
 */
public Map<String, Object> getWorkflowStatus(Map<String, Object> params) {
    // Implementation
}

/**
 * MCP Tool: List pending actions
 */
public Map<String, Object> listPendingActions(Map<String, Object> params) {
    // Implementation
}

/**
 * MCP Tool: Complete action
 */
public Map<String, Object> completeAction(Map<String, Object> params) {
    // Implementation
}
```

---

## 🧪 Testing Strategy

### Unit Tests
```java
@SpringBootTest
class WorkflowServiceTest {
    
    @Test
    void testStartWorkflow() {
        // Test workflow creation
    }
    
    @Test
    void testValidTransitions() {
        // Test all valid state transitions
    }
    
    @Test
    void testInvalidTransitions() {
        // Test that invalid transitions are rejected
    }
    
    @Test
    void testWorkflowHistory() {
        // Test history tracking
    }
}
```

### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class WorkflowIntegrationTest {
    
    @Test
    void testEndToEndWorkflow() {
        // Create event via email
        // Verify automated processing
        // Check workflow state
        // Verify notifications sent
    }
}
```

---

## 📊 Success Metrics

Track these metrics during Phase 2:

| Metric | Target | How to Measure |
|--------|--------|----------------|
| Automated Processing Rate | >90% | Events auto-processed / Total events |
| Processing Time | <60 sec | Time from creation to completion |
| Follow-up Accuracy | >85% | Correct actions / Total actions |
| Email Notification Success | 100% | Emails sent / Required notifications |
| API Response Time | <500ms | Average response time |
| Test Coverage | >80% | Lines covered / Total lines |

---

## 🚀 Getting Started

### Day 1 Checklist

1. **Review Phase 1 Work**
   - [ ] Read `DOCUMENT_CLASSIFICATION_GUIDE.md`
   - [ ] Test existing email classification
   - [ ] Review `McpServer.java` structure

2. **Set Up Development Environment**
   - [ ] Pull latest code
   - [ ] Create branch: `feature/phase2-workflow`
   - [ ] Set up IDE for async debugging

3. **Design Session**
   - [ ] Draw workflow state diagram
   - [ ] Document state transition rules
   - [ ] Design database schema

4. **Start Coding**
   - [ ] Create `Workflow` entity
   - [ ] Create `WorkflowHistory` entity
   - [ ] Start `WorkflowService` implementation

---

## 📚 Reference Documents

- [PROJECT_PLAN.md](PROJECT_PLAN.md) - Complete project plan
- [ROADMAP.md](ROADMAP.md) - Visual roadmap
- [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - Phase 1 summary
- [DOCUMENT_CLASSIFICATION_GUIDE.md](DOCUMENT_CLASSIFICATION_GUIDE.md) - Phase 1 usage

---

## 🤝 Team Collaboration

### Daily Standup (15 min)
- What did I complete yesterday?
- What will I work on today?
- Any blockers?

### Weekly Review (Friday, 1 hour)
- Demo completed features
- Review metrics
- Plan next week
- Risk assessment

### Communication Channels
- Slack: #phase2-workflow
- GitHub: feature/phase2-workflow branch
- Jira: PHARMA-2XX tickets

---

## ⚠️ Risks & Mitigation

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Async processing complexity | Medium | Medium | Start simple, add complexity gradually |
| State machine bugs | High | Medium | Comprehensive testing, clear documentation |
| Email notification failures | Low | Low | Implement retry mechanism |
| Performance issues | Medium | Low | Early load testing, optimize queries |

---

## 🎯 Definition of Done (Phase 2)

Phase 2 is complete when:

- [ ] All Week 5 tasks completed
- [ ] All Week 6 tasks completed
- [ ] All Week 7 tasks completed
- [ ] Workflow state machine working correctly
- [ ] Automated processing triggered on email classification
- [ ] Scheduled tasks running as expected
- [ ] All 8 workflow API endpoints implemented
- [ ] All 4 new MCP tools working
- [ ] Test coverage >80%
- [ ] Documentation complete
- [ ] Demo successful
- [ ] Phase 2 review meeting completed
- [ ] No critical bugs

---

## 📞 Support & Questions

**Technical Lead:** [Name]  
**Project Manager:** [Name]  
**Slack:** #phase2-workflow  
**Email:** team@pharmacovigilance.com

---

## 🎉 Let's Build!

You're ready to start Phase 2! The foundation from Phase 1 is solid, and you have a clear plan ahead.

**First Step:** Create the workflow state machine design document.

**Good luck! 🚀**

