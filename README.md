# Pharmacovigilance MCP Agent

A Spring Boot application that integrates Model Context Protocol (MCP) and AI agents for automated pharmacovigilance workflow management. This system provides intelligent analysis of adverse drug events using ChatGPT integration and automated processing workflows.

**Current Status:** ✅ Phase 1 Complete | 🟡 Phase 2 Ready to Start  
**Project Timeline:** 16 weeks (5 phases)  
**Completion:** 20% (4 of 16 weeks)

## Features

- **MCP Server Integration**: RESTful API endpoints for pharmacovigilance data access
- **AI-Powered Analysis**: ChatGPT integration for causality assessment and risk analysis
- **Document-Based Classification**: Extract and classify adverse events from PDF, emails, and telephony transcripts using AI
- **Multi-Format Document Parsing**: Support for PDF, Word, email (.eml), and other document formats
- **Automated Workflows**: Intelligent agent-based processing of adverse events
- **Comprehensive Data Models**: Patient, Drug, Adverse Event, and Follow-up Action entities
- **Real-time Processing**: Asynchronous processing with scheduled tasks
- **Pattern Detection**: AI-driven analysis of adverse event patterns and trends
- **Comprehensive API Documentation**: Interactive Swagger UI with examples and testing capabilities

## API Documentation Features

The application includes comprehensive Swagger/OpenAPI documentation with:

- **Interactive API Explorer**: Test all endpoints directly from the browser
- **Detailed Request/Response Examples**: Pre-configured examples for all operations
- **Schema Documentation**: Complete data model documentation
- **MCP Tools Documentation**: Detailed documentation for all MCP tools
- **Try It Out Functionality**: Execute API calls directly from the documentation
- **Response Examples**: Multiple response examples for different scenarios

## Architecture

### Core Components

1. **Domain Models**
   - `AdverseEvent`: Core entity for adverse drug event reports
   - `Patient`: Patient information and medical history
   - `Drug`: Drug information and safety profiles
   - `FollowUpAction`: Automated follow-up actions and tasks
   - `AiAnalysis`: AI analysis results and insights

2. **MCP Server**
   - RESTful API for external tool integration
   - Standardized data access patterns
   - Tool discovery and execution endpoints

3. **AI Integration**
   - OpenAI ChatGPT integration for intelligent analysis
   - Causality assessment automation
   - Risk analysis and pattern detection
   - Automated recommendations generation
   - Document extraction and classification from unstructured text

4. **Document Processing**
   - PDF parsing using Apache PDFBox
   - Email parsing (text and .eml files)
   - Multi-format support via Apache Tika
   - AI-powered structured data extraction

5. **Agent Workflow**
   - Automated adverse event processing
   - Scheduled pattern analysis
   - Follow-up action generation
   - Status management automation

## Document-Based Classification 📄

The system now supports extracting and classifying adverse events from various document sources:

- **PDF Documents**: Clinical reports, medical records
- **Email**: Direct text or .eml file format
- **Telephony Transcripts**: Phone call transcripts
- **Any Document Format**: Word, text files, RTF, etc.

### Quick Examples

**Classify from PDF:**
```bash
curl -X POST http://localhost:8080/api/mcp/tools/classify_from_pdf \
  -H "Content-Type: application/json" \
  -d '{"pdfContent": "BASE64_ENCODED_PDF", "fileName": "report.pdf"}'
```

**Classify from Email:**
```bash
curl -X POST http://localhost:8080/api/mcp/tools/classify_from_email \
  -H "Content-Type: application/json" \
  -d '{"subject": "Adverse Event", "body": "Patient reported...", "sender": "doctor@hospital.com"}'
```

**Classify from Telephony:**
```bash
curl -X POST http://localhost:8080/api/mcp/tools/classify_from_telephony_transcript \
  -H "Content-Type: application/json" \
  -d '{"transcript": "Caller: Patient developed rash...", "callerInfo": "Jane Doe"}'
```

For detailed documentation, see [DOCUMENT_CLASSIFICATION_GUIDE.md](DOCUMENT_CLASSIFICATION_GUIDE.md)

## 📋 Project Planning

This project follows a phased approach:

| Phase | Focus | Duration | Status |
|-------|-------|----------|--------|
| **Phase 1** | MCP + Email Processing | 4 weeks | ✅ COMPLETED |
| **Phase 2** | Workflow API + Automation | 3 weeks | 🟡 NEXT |
| **Phase 3** | Multi-Channel Processing | 4 weeks | ⚪ PLANNED |
| **Phase 4** | Advanced AI & Analytics | 3 weeks | ⚪ PLANNED |
| **Phase 5** | Production Deployment | 2 weeks | ⚪ PLANNED |

### Planning Documents

- 📖 **[PROJECT_PLAN.md](PROJECT_PLAN.md)** - Complete 16-week project plan with detailed specs
- 🗺️ **[ROADMAP.md](ROADMAP.md)** - Visual roadmap with feature timeline
- 🚀 **[PHASE2_KICKOFF.md](PHASE2_KICKOFF.md)** - Ready-to-start guide for Phase 2
- 📝 **[PLANNING_SUMMARY.md](PLANNING_SUMMARY.md)** - Quick reference summary
- 📊 **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Phase 1 technical summary
- 📘 **[DOCUMENT_CLASSIFICATION_GUIDE.md](DOCUMENT_CLASSIFICATION_GUIDE.md)** - Usage guide

### Presentation Materials

- 🎤 **[PRESENTATION.md](PRESENTATION.md)** - 20-slide presentation deck (ready for PowerPoint/PDF)
- 📄 **[EXECUTIVE_SUMMARY.md](EXECUTIVE_SUMMARY.md)** - One-page executive summary
- 🎨 **[PRESENTATION_GUIDE.md](PRESENTATION_GUIDE.md)** - How to convert documents to slides
- 📇 **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Printable quick reference card
- 📊 **[PROJECT_TIMELINE.csv](PROJECT_TIMELINE.csv)** - Detailed timeline (60+ tasks, Excel-ready)
- 🎯 **[MILESTONE_ROADMAP.csv](MILESTONE_ROADMAP.csv)** - High-level milestones (5 phases)
- 📈 **[EXCEL_TIMELINE_GUIDE.md](EXCEL_TIMELINE_GUIDE.md)** - Create Gantt charts in Excel

### What's Next: Phase 2 - Workflow API

Coming in the next 3 weeks:
- Workflow state machine (NEW → REVIEW → INVESTIGATION → CONFIRMED → CLOSED)
- Automated event processing on email classification
- Follow-up action generation
- Email notifications
- Scheduled tasks (every 5 min + daily pattern analysis)
- 8 new REST API endpoints
- 4 new MCP tools

See [PHASE2_KICKOFF.md](PHASE2_KICKOFF.md) to get started!

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- OpenAI API key

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd pharmacovigilance-mcp-agent
```

2. Set up environment variables:
```bash
export OPENAI_API_KEY=your-openai-api-key-here
```

3. Build the application:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/pharmacovigilance`

### API Documentation

- **Swagger UI**: `http://localhost:8080/pharmacovigilance/swagger-ui.html`
- **API Documentation**: `http://localhost:8080/pharmacovigilance/api-docs`
- **OpenAPI JSON**: `http://localhost:8080/pharmacovigilance/v3/api-docs`

### Database Access

The application uses H2 in-memory database for development. Access the H2 console at:
`http://localhost:8080/pharmacovigilance/h2-console`

- JDBC URL: `jdbc:h2:mem:pharmacovigilance`
- Username: `sa`
- Password: `password`

## API Endpoints

### MCP Tools

- `GET /api/mcp/tools` - List available MCP tools
- `POST /api/mcp/tools/{toolName}` - Execute MCP tool

Available tools:
- `get_adverse_events` - Retrieve adverse events by criteria
- `get_patient_info` - Get patient information
- `get_drug_info` - Get drug information
- `create_adverse_event` - Create new adverse event
- `update_adverse_event_status` - Update event status
- `get_statistics` - Get pharmacovigilance statistics

### REST APIs

#### Adverse Events
- `GET /api/adverse-events` - Get all adverse events (paginated)
- `GET /api/adverse-events/{id}` - Get adverse event by ID
- `POST /api/adverse-events` - Create new adverse event
- `PUT /api/adverse-events/{id}` - Update adverse event
- `DELETE /api/adverse-events/{id}` - Delete adverse event
- `GET /api/adverse-events/search` - Search adverse events
- `GET /api/adverse-events/statistics` - Get statistics
- `PATCH /api/adverse-events/{id}/status` - Update event status

#### Patients
- `GET /api/patients` - Get all patients (paginated)
- `GET /api/patients/{id}` - Get patient by ID
- `GET /api/patients/patient-id/{patientId}` - Get patient by patient ID
- `POST /api/patients` - Create new patient
- `PUT /api/patients/{id}` - Update patient
- `DELETE /api/patients/{id}` - Delete patient
- `GET /api/patients/search` - Search patients by last name

#### Drugs
- `GET /api/drugs` - Get all drugs (paginated)
- `GET /api/drugs/{id}` - Get drug by ID
- `GET /api/drugs/drug-code/{drugCode}` - Get drug by drug code
- `POST /api/drugs` - Create new drug
- `PUT /api/drugs/{id}` - Update drug
- `DELETE /api/drugs/{id}` - Delete drug
- `GET /api/drugs/search` - Search drugs by name
- `GET /api/drugs/manufacturer/{manufacturer}` - Get drugs by manufacturer

## Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
# OpenAI Configuration
openai:
  api-key: ${OPENAI_API_KEY:your-openai-api-key-here}
  model: gpt-3.5-turbo
  timeout: 60s

# Agent Configuration
pharmacovigilance:
  agent:
    enabled: true
    processing-delay: 5000
    batch-size: 10
```

### Environment Variables

- `OPENAI_API_KEY`: Your OpenAI API key (required for AI features)

## Usage Examples

### Creating an Adverse Event

```bash
curl -X POST http://localhost:8080/pharmacovigilance/api/adverse-events \
  -H "Content-Type: application/json" \
  -d '{
    "caseNumber": "AE-2024-001",
    "patientId": "P001",
    "drugName": "Aspirin",
    "adverseEventDescription": "Severe headache and nausea",
    "severity": "MODERATE",
    "status": "NEW",
    "symptoms": "Headache, nausea, dizziness",
    "eventDate": "2024-01-15T10:30:00"
  }'
```

### Using MCP Tools

```bash
# Get adverse events by severity
curl -X POST http://localhost:8080/pharmacovigilance/api/mcp/tools/get_adverse_events \
  -H "Content-Type: application/json" \
  -d '{"severity": "SEVERE"}'

# Get statistics
curl -X POST http://localhost:8080/pharmacovigilance/api/mcp/tools/get_statistics \
  -H "Content-Type: application/json" \
  -d '{}'
```

## Automated Workflows

The application includes several automated workflows:

1. **New Adverse Event Processing**: Automatically triggered when a new adverse event is created
   - AI causality assessment
   - Risk analysis
   - Follow-up action generation
   - Status updates

2. **Scheduled Pattern Analysis**: Runs daily at 2 AM
   - Analyzes patterns across adverse events
   - Identifies potential safety signals
   - Generates recommendations

3. **Pending Event Processing**: Runs every 5 minutes
   - Processes events that have been pending for more than 5 minutes
   - Ensures no events are left unprocessed

## Development

### Project Structure

```
src/main/java/com/pharmacovigilance/mcpagent/
├── agent/                 # Automated workflow agents
├── config/               # Configuration classes
├── controller/           # REST API controllers
├── dto/                  # Data Transfer Objects
├── mcp/                  # MCP server implementation
├── model/                # Domain entities
├── repository/           # Data access layer
└── service/              # Business logic services
    ├── AdverseEventService
    ├── AiAnalysisService
    ├── AdverseEventExtractionService  # NEW: AI-powered extraction
    ├── DocumentParsingService         # NEW: PDF/Email/Document parsing
    ├── DrugService
    └── PatientService
```

### Building and Testing

```bash
# Run tests
mvn test

# Build with tests
mvn clean package

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please open an issue in the repository or contact the development team.
