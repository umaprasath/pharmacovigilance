# Pharmacovigilance MCP Agent - Project Roadmap

## 🗓️ Timeline Overview

```
Weeks 1-4  │ ✅ PHASE 1: MCP + Email Processing
           │
Weeks 5-7  │ 🟡 PHASE 2: Workflow API + Automation
           │
Weeks 8-11 │ ⚪ PHASE 3: Multi-Channel Processing
           │
Weeks 12-14│ ⚪ PHASE 4: Advanced AI & Analytics
           │
Weeks 15-16│ ⚪ PHASE 5: Production Deployment
```

---

## Phase 1: MCP + Email Processing ✅ COMPLETED

**Duration:** 4 weeks  
**Status:** 🟢 COMPLETED

### What Was Built

```
┌─────────────────────────────────────────┐
│         MCP Server Core                 │
│  ┌────────────────────────────────┐    │
│  │ Tool Discovery & Execution     │    │
│  │ Error Handling                 │    │
│  │ REST API Gateway               │    │
│  └────────────────────────────────┘    │
└──────────────┬──────────────────────────┘
               │
      ┌────────┴────────┐
      │                 │
┌─────▼─────┐    ┌─────▼──────────┐
│   Email   │    │   AI Extract   │
│ Processing│    │   (OpenAI)     │
└───────────┘    └────────────────┘
```

### Key Features
- ✅ MCP server with 8 tools
- ✅ Email parsing (text + .eml files)
- ✅ AI-powered data extraction
- ✅ Causality assessment
- ✅ Risk analysis
- ✅ REST API endpoints
- ✅ Swagger documentation

### API Endpoints (Phase 1)
```bash
POST /api/mcp/tools/classify_from_email          # ⭐ Core Feature
POST /api/mcp/tools/get_adverse_events
POST /api/mcp/tools/create_adverse_event
POST /api/mcp/tools/get_statistics
GET  /api/mcp/tools                               # Tool discovery
```

### Tech Stack
- Spring Boot 3.2.0
- OpenAI API
- JavaMail (email parsing)
- H2/PostgreSQL
- Swagger/OpenAPI

---

## Phase 2: Workflow API + Automation 🟡 NEXT

**Duration:** 3 weeks  
**Status:** 🟡 IN PLANNING

### What Will Be Built

```
┌─────────────────────────────────────────┐
│        Workflow Engine                  │
│  ┌────────────────────────────────┐    │
│  │ State Machine                  │    │
│  │ Automated Processing           │    │
│  │ Follow-up Actions              │    │
│  │ Scheduled Tasks                │    │
│  └────────────────────────────────┘    │
└──────────────┬──────────────────────────┘
               │
      ┌────────┼────────┐
      │        │        │
┌─────▼──┐ ┌──▼───┐ ┌──▼──────┐
│Workflow│ │Async │ │Schedule │
│  API   │ │Tasks │ │  Jobs   │
└────────┘ └──────┘ └─────────┘
```

### Key Features
- [ ] Workflow state machine (NEW → REVIEW → INVESTIGATION → CONFIRMED → CLOSED)
- [ ] Automated event processing on creation
- [ ] Follow-up action generation
- [ ] Email notifications
- [ ] Scheduled tasks
  - Pending event processor (every 5 min)
  - Daily pattern analysis (2 AM)
- [ ] Workflow API endpoints

### New API Endpoints (Phase 2)
```bash
# Workflow Management
POST /api/workflow/start                          # Start workflow
POST /api/workflow/{id}/transition                # Change state
GET  /api/workflow/{id}/status                    # Check status
POST /api/workflow/{id}/assign                    # Assign to user

# Follow-up Actions
GET  /api/follow-up/pending                       # List pending
POST /api/follow-up/{id}/complete                 # Complete action

# Automation
POST /api/automation/process-event/{id}           # Trigger processing

# MCP Workflow Tools (4 new tools)
POST /api/mcp/tools/start_workflow
POST /api/mcp/tools/get_workflow_status
POST /api/mcp/tools/list_pending_actions
POST /api/mcp/tools/complete_action
```

### Workflow States
```
     NEW
      │
      ▼
UNDER_REVIEW ──────► REJECTED
      │
      ▼
INVESTIGATION
      │
      ▼
  CONFIRMED
      │
      ▼
    CLOSED
```

### Automation Flow
```
Email Received
     │
     ▼
Create Event (classify_from_email)
     │
     ▼
Trigger Automated Processing
     │
     ├──► AI Causality Assessment
     ├──► AI Risk Analysis
     ├──► Generate Follow-up Actions
     └──► Update Event Status
     │
     ▼
Send Email Notification
```

### Success Metrics
- [ ] >90% events auto-processed
- [ ] <60 seconds processing time
- [ ] >85% follow-up accuracy
- [ ] 100% email workflow integration

---

## Phase 3: Multi-Channel Processing ⚪ PLANNED

**Duration:** 4 weeks  
**Status:** ⚪ PLANNED

### What Will Be Built

```
┌─────────────────────────────────────────┐
│    Multi-Channel Input Processing      │
│  ┌────────────────────────────────┐    │
│  │ PDF → Text                     │    │
│  │ Telephony → Text               │    │
│  │ Word → Text                    │    │
│  │ Any Format → Text              │    │
│  └────────────────────────────────┘    │
└──────────────┬──────────────────────────┘
               │
      ┌────────┼────────┐
      │        │        │
┌─────▼──┐ ┌──▼───┐ ┌──▼──────┐
│  PDF   │ │ Phone│ │Universal│
│Parser  │ │Trans.│ │ Parser  │
└────────┘ └──────┘ └─────────┘
```

### Key Features
- [ ] PDF document processing (Apache PDFBox)
- [ ] Telephony transcript processing
- [ ] Universal document parser (Apache Tika)
- [ ] OCR for scanned documents
- [ ] Batch processing capability
- [ ] Document queue management

### New API Endpoints (Phase 3)
```bash
POST /api/mcp/tools/classify_from_pdf              # PDF processing
POST /api/mcp/tools/classify_from_telephony_transcript
POST /api/mcp/tools/classify_from_document         # Universal

POST /api/documents/upload                          # Upload document
POST /api/documents/batch                           # Batch upload
GET  /api/documents/queue-status                    # Queue status
```

### Supported Formats
- ✅ Email (text, .eml)
- 📄 PDF
- 📞 Text transcripts
- 📝 Word (.doc, .docx)
- 📋 Plain text
- 🖼️ Image + OCR (optional)

### Success Metrics
- [ ] PDF accuracy: >85%
- [ ] Support 5+ formats
- [ ] 100 documents/hour
- [ ] >80% extraction quality

---

## Phase 4: Advanced AI & Analytics ⚪ PLANNED

**Duration:** 3 weeks  
**Status:** ⚪ PLANNED

### What Will Be Built

```
┌─────────────────────────────────────────┐
│      Analytics & Intelligence          │
│  ┌────────────────────────────────┐    │
│  │ Pattern Detection              │    │
│  │ Trend Analysis                 │    │
│  │ Predictive Models              │    │
│  │ Dashboard API                  │    │
│  └────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

### Key Features
- [ ] Pattern detection across events
- [ ] Signal detection algorithms
- [ ] Trend analysis
- [ ] Dashboard API
- [ ] Report generation
- [ ] GPT-4 integration
- [ ] Confidence scoring
- [ ] Multi-language support

### New API Endpoints (Phase 4)
```bash
# Analytics
GET  /api/analytics/dashboard                      # Dashboard data
GET  /api/analytics/trends                         # Trends
GET  /api/analytics/patterns                       # Patterns

# Reports
POST /api/reports/generate                         # Generate report
GET  /api/reports/{id}/download                    # Download
```

### Success Metrics
- [ ] Pattern detection: >75%
- [ ] Dashboard: <2 sec load
- [ ] Reports: <30 sec generation
- [ ] AI confidence: >0.8 avg

---

## Phase 5: Production Deployment ⚪ PLANNED

**Duration:** 2 weeks  
**Status:** ⚪ PLANNED

### What Will Be Built

```
┌─────────────────────────────────────────┐
│    Production Infrastructure           │
│  ┌────────────────────────────────┐    │
│  │ Security Hardening             │    │
│  │ Performance Optimization       │    │
│  │ Monitoring & Alerting          │    │
│  │ CI/CD Pipeline                 │    │
│  └────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

### Key Features
- [ ] OAuth2/JWT authentication
- [ ] Rate limiting
- [ ] HIPAA compliance
- [ ] Docker containerization
- [ ] Kubernetes deployment
- [ ] CI/CD pipeline
- [ ] Monitoring (Prometheus + Grafana)
- [ ] Logging (ELK Stack)
- [ ] Backup & disaster recovery

### Infrastructure
- [ ] Load balancer
- [ ] Redis caching
- [ ] RabbitMQ message queue
- [ ] S3 document storage
- [ ] Production database

### Success Metrics
- [ ] Uptime: >99.5%
- [ ] Security: 0 critical vulnerabilities
- [ ] Deployment: <10 minutes
- [ ] MTTR: <30 minutes

---

## Feature Comparison by Phase

| Feature | Phase 1 | Phase 2 | Phase 3 | Phase 4 | Phase 5 |
|---------|---------|---------|---------|---------|---------|
| MCP Server | ✅ | ✅ | ✅ | ✅ | ✅ |
| Email Processing | ✅ | ✅ | ✅ | ✅ | ✅ |
| Workflow API | ❌ | ✅ | ✅ | ✅ | ✅ |
| Automated Processing | ❌ | ✅ | ✅ | ✅ | ✅ |
| PDF Processing | ❌ | ❌ | ✅ | ✅ | ✅ |
| Telephony Processing | ❌ | ❌ | ✅ | ✅ | ✅ |
| Pattern Detection | ❌ | ❌ | ❌ | ✅ | ✅ |
| Analytics Dashboard | ❌ | ❌ | ❌ | ✅ | ✅ |
| Production Ready | ❌ | ❌ | ❌ | ❌ | ✅ |

---

## MCP Tools Growth

### Phase 1 ✅ (8 tools)
```
✅ classify_from_email
✅ get_adverse_events
✅ get_patient_info
✅ get_drug_info
✅ create_adverse_event
✅ update_adverse_event_status
✅ classify_event_from_input
✅ get_statistics
```

### Phase 2 🟡 (+4 tools)
```
⏳ start_workflow
⏳ get_workflow_status
⏳ list_pending_actions
⏳ complete_action
```

### Phase 3 ⚪ (+3 tools)
```
⏳ classify_from_pdf
⏳ classify_from_telephony_transcript
⏳ classify_from_document
```

### Phase 4 ⚪ (+3 tools)
```
⏳ detect_patterns
⏳ get_analytics_dashboard
⏳ generate_report
```

**Total MCP Tools by End: 18 tools**

---

## Integration Points by Phase

### Phase 1: Foundation
```
External System → MCP API → Email Processing → Database
```

### Phase 2: Workflow
```
External System → MCP API → Workflow Engine → Automated Tasks
                                    ↓
                            Email Notifications
```

### Phase 3: Multi-Channel
```
PDF/Email/Phone → MCP API → Document Parser → AI Extract → Workflow
```

### Phase 4: Intelligence
```
All Channels → Processing → Analytics Engine → Insights → Dashboard
```

### Phase 5: Production
```
Load Balancer → API Gateway → Services → Queue → Cache → Database
       ↓
   Monitoring & Alerting
```

---

## Quick Start Guide by Phase

### Using Phase 1 (Current)
```bash
# Classify email
curl -X POST http://localhost:8080/api/mcp/tools/classify_from_email \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Adverse Event Report",
    "body": "Patient reported severe headache after aspirin",
    "sender": "doctor@hospital.com"
  }'
```

### Using Phase 2 (After Completion)
```bash
# Start workflow for an event
curl -X POST http://localhost:8080/api/mcp/tools/start_workflow \
  -H "Content-Type: application/json" \
  -d '{"eventId": 1, "assignee": "safety_team@hospital.com"}'

# Check workflow status
curl -X POST http://localhost:8080/api/mcp/tools/get_workflow_status \
  -H "Content-Type: application/json" \
  -d '{"workflowId": 1}'
```

### Using Phase 3 (After Completion)
```bash
# Classify PDF
curl -X POST http://localhost:8080/api/mcp/tools/classify_from_pdf \
  -H "Content-Type: application/json" \
  -d '{"pdfContent": "BASE64_PDF", "fileName": "report.pdf"}'

# Classify phone transcript
curl -X POST http://localhost:8080/api/mcp/tools/classify_from_telephony_transcript \
  -H "Content-Type: application/json" \
  -d '{"transcript": "Patient called about rash...", "callId": "CALL-001"}'
```

---

## Current Status Dashboard

```
Phase 1: ████████████████████████ 100% ✅
Phase 2: ░░░░░░░░░░░░░░░░░░░░░░░░   0% 🟡
Phase 3: ░░░░░░░░░░░░░░░░░░░░░░░░   0% ⚪
Phase 4: ░░░░░░░░░░░░░░░░░░░░░░░░   0% ⚪
Phase 5: ░░░░░░░░░░░░░░░░░░░░░░░░   0% ⚪

Overall: ████░░░░░░░░░░░░░░░░░░░░  20%
```

**Estimated Time to MVP (Phase 2):** 3 weeks  
**Estimated Time to Full Production:** 12 weeks

---

## Decision Points

### After Phase 1 ✅
- ✅ Continue with workflow automation (Phase 2)
- ✅ Email processing accuracy validated
- ✅ MCP integration working well

### After Phase 2 🟡
- [ ] Evaluate multi-channel need
- [ ] Assess workflow adoption
- [ ] Decide on Phase 3 scope

### After Phase 3 ⚪
- [ ] Review analytics requirements
- [ ] Validate pattern detection need
- [ ] Plan Phase 4 features

### After Phase 4 ⚪
- [ ] Production deployment strategy
- [ ] Infrastructure sizing
- [ ] Go-live date

---

## Resources Required

### Development Team
- **Phase 1:** 2 developers (completed)
- **Phase 2:** 3 developers + 1 QA
- **Phase 3:** 3 developers + 1 QA
- **Phase 4:** 2 developers + 1 data scientist
- **Phase 5:** 2 developers + 1 DevOps + 1 QA

### Budget
- **Phase 1:** $41,000 ✅
- **Phase 2:** $32,000 🟡
- **Phase 3:** $43,000 ⚪
- **Phase 4:** $33,000 ⚪
- **Phase 5:** $24,000 ⚪
- **Total:** $173,000

### Timeline
- **Completed:** 4 weeks (Phase 1)
- **Remaining:** 12 weeks (Phases 2-5)
- **Total:** 16 weeks

---

## Next Immediate Actions

### Week 5 (Phase 2 Start)
1. [ ] Create workflow state machine design
2. [ ] Set up async processing framework
3. [ ] Design follow-up action schema
4. [ ] Create Phase 2 user stories
5. [ ] Start WorkflowService implementation

---

## Documentation
- 📋 [PROJECT_PLAN.md](PROJECT_PLAN.md) - Detailed project plan
- 📖 [DOCUMENT_CLASSIFICATION_GUIDE.md](DOCUMENT_CLASSIFICATION_GUIDE.md) - Phase 1 usage guide
- 📊 [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - Technical summary
- 🗺️ [ROADMAP.md](ROADMAP.md) - This document

---

## Contact
**Project Repository:** [GitHub Link]  
**Documentation:** [Wiki Link]  
**Issue Tracker:** [Issues Link]

---

**Legend:**
- ✅ Completed
- 🟡 In Progress
- ⚪ Planned
- 🟢 On Track
- 🟠 At Risk
- 🔴 Blocked

