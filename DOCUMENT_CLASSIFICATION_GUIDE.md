# Document-Based Adverse Event Classification Guide

This guide explains how to classify adverse events from various document sources (PDF, Email, Telephony transcripts) using OpenAI.

## Overview

The system now supports extracting and classifying adverse events from:
- **PDF Documents** - Clinical reports, medical records
- **Email Messages** - Both direct text and .eml files
- **Telephony Transcripts** - Phone call transcripts
- **Any Document Format** - Word, text files, etc. (via Apache Tika)

## Architecture

### Components

1. **AdverseEventExtractionService** - Uses OpenAI to extract structured data from unstructured text
2. **DocumentParsingService** - Parses PDF, email, and other document formats
3. **McpServer** - Provides MCP tools for document-based classification
4. **McpController** - REST API endpoints

### Flow

```
Document (PDF/Email/Text) 
  → Parse/Extract Text 
  → OpenAI Extraction (structured data) 
  → Validation 
  → OpenAI Classification (causality + risk)
  → Results
```

## API Endpoints

### 1. Classify from PDF Document

**Endpoint:** `POST /api/mcp/tools/classify_from_pdf`

**Request:**
```json
{
  "pdfContent": "BASE64_ENCODED_PDF_CONTENT",
  "fileName": "clinical_report.pdf"
}
```

**Response:**
```json
{
  "success": true,
  "fileName": "clinical_report.pdf",
  "extractedText": "First 500 characters of extracted text...",
  "extractedData": {
    "drugName": "Aspirin",
    "adverseEventDescription": "Severe stomach bleeding",
    "severity": "SEVERE",
    "symptoms": "Bloody stool, abdominal pain",
    "patientAge": "65",
    "patientGender": "MALE",
    "medicalHistory": "History of gastric ulcers",
    "concomitantMedications": "Warfarin",
    "isValid": true,
    "extractionTimestamp": 1234567890,
    "extractionSource": "OpenAI-gpt-3.5-turbo"
  },
  "classification": {
    "causalityAssessment": {
      "analysisId": 1,
      "type": "CAUSALITY_ASSESSMENT",
      "status": "COMPLETED",
      "insights": "Key factors extracted...",
      "recommendations": "Recommendations...",
      "fullResponse": "Complete AI analysis..."
    },
    "riskAnalysis": {
      "analysisId": 2,
      "type": "RISK_ANALYSIS",
      "status": "COMPLETED",
      "insights": "Risk insights...",
      "recommendations": "Risk recommendations...",
      "fullResponse": "Complete risk analysis..."
    }
  },
  "message": "PDF processed and adverse event classified successfully"
}
```

**cURL Example:**
```bash
# Convert PDF to Base64
PDF_BASE64=$(base64 -i clinical_report.pdf)

curl -X POST http://localhost:8080/api/mcp/tools/classify_from_pdf \
  -H "Content-Type: application/json" \
  -d "{
    \"pdfContent\": \"$PDF_BASE64\",
    \"fileName\": \"clinical_report.pdf\"
  }"
```

### 2. Classify from Email

**Endpoint:** `POST /api/mcp/tools/classify_from_email`

**Option A: Direct Email Content**
```json
{
  "subject": "Adverse Event Report - Patient ID 12345",
  "body": "Patient reported severe headache after taking Ibuprofen 400mg...",
  "sender": "doctor@hospital.com"
}
```

**Option B: .eml File**
```json
{
  "emlContent": "BASE64_ENCODED_EML_FILE"
}
```

**Response:**
```json
{
  "success": true,
  "emailMetadata": {
    "from": "doctor@hospital.com",
    "subject": "Adverse Event Report - Patient ID 12345"
  },
  "extractedData": {
    "drugName": "Ibuprofen",
    "adverseEventDescription": "Severe headache",
    "severity": "MODERATE",
    "symptoms": "Throbbing headache, dizziness",
    "reporterType": "PHYSICIAN",
    "isValid": true
  },
  "classification": { /* ... */ },
  "message": "Email processed and adverse event classified successfully"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/mcp/tools/classify_from_email \
  -H "Content-Type: application/json" \
  -d '{
    "subject": "Adverse Event Report",
    "body": "Patient reported severe headache after taking Ibuprofen 400mg. Symptoms started 2 hours after dose.",
    "sender": "doctor@hospital.com"
  }'
```

### 3. Classify from Telephony Transcript

**Endpoint:** `POST /api/mcp/tools/classify_from_telephony_transcript`

**Request:**
```json
{
  "transcript": "Caller: Hi, I'm calling about my mother. She took Lisinopril yesterday and developed a severe cough. Operator: Can you describe the symptoms? Caller: She's been coughing constantly, it's very dry and painful...",
  "callerInfo": "Jane Doe (daughter of patient)",
  "callId": "CALL-2024-001"
}
```

**Response:**
```json
{
  "success": true,
  "callMetadata": {
    "callId": "CALL-2024-001",
    "callerInfo": "Jane Doe (daughter of patient)"
  },
  "extractedData": {
    "drugName": "Lisinopril",
    "adverseEventDescription": "Severe dry cough",
    "severity": "MODERATE",
    "symptoms": "Constant dry cough, painful",
    "reporterType": "PATIENT",
    "isValid": true
  },
  "classification": { /* ... */ },
  "message": "Telephony transcript processed and adverse event classified successfully"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/mcp/tools/classify_from_telephony_transcript \
  -H "Content-Type: application/json" \
  -d '{
    "transcript": "Caller: Hi, I am calling about my mother. She took Lisinopril yesterday and developed a severe cough...",
    "callerInfo": "Jane Doe (daughter)",
    "callId": "CALL-2024-001"
  }'
```

### 4. Classify from Any Document

**Endpoint:** `POST /api/mcp/tools/classify_from_document`

Supports: PDF, Word (.doc, .docx), text files, RTF, etc.

**Request:**
```json
{
  "documentContent": "BASE64_ENCODED_DOCUMENT",
  "fileName": "report.docx",
  "documentType": "auto"
}
```

**Response:** Similar to PDF classification

**cURL Example:**
```bash
# Convert document to Base64
DOC_BASE64=$(base64 -i report.docx)

curl -X POST http://localhost:8080/api/mcp/tools/classify_from_document \
  -H "Content-Type: application/json" \
  -d "{
    \"documentContent\": \"$DOC_BASE64\",
    \"fileName\": \"report.docx\"
  }"
```

## Extracted Data Fields

The AI extraction service attempts to extract the following fields:

| Field | Description | Required |
|-------|-------------|----------|
| `drugName` | Name of the drug involved | ✓ |
| `adverseEventDescription` | Description of adverse event | ✓ |
| `severity` | MILD, MODERATE, SEVERE, LIFE_THREATENING, FATAL | - |
| `symptoms` | List of symptoms | - |
| `patientAge` | Patient age | - |
| `patientGender` | MALE, FEMALE, OTHER | - |
| `medicalHistory` | Relevant medical history | - |
| `concomitantMedications` | Other medications | - |
| `dateOfOnset` | When event started | - |
| `outcome` | Current outcome/status | - |
| `reporterName` | Name of reporter | - |
| `reporterType` | PHYSICIAN, NURSE, PHARMACIST, PATIENT, OTHER | - |
| `additionalNotes` | Other relevant information | - |

## Classification Results

For each valid extraction, the system performs two AI classifications:

### 1. Causality Assessment
- Evaluates the relationship between drug and adverse event
- Classification: Certain, Probable, Possible, Unlikely, Unclassifiable, Unassessable
- Provides reasoning and key factors

### 2. Risk Analysis
- Evaluates patient safety risk
- Risk levels: Low, Medium, High, Critical
- Provides impact assessment and mitigation strategies

## Integration Examples

### Python Example
```python
import requests
import base64

def classify_pdf(pdf_path):
    # Read and encode PDF
    with open(pdf_path, 'rb') as f:
        pdf_base64 = base64.b64encode(f.read()).decode('utf-8')
    
    # Make API request
    response = requests.post(
        'http://localhost:8080/api/mcp/tools/classify_from_pdf',
        json={
            'pdfContent': pdf_base64,
            'fileName': pdf_path.split('/')[-1]
        }
    )
    
    return response.json()

# Usage
result = classify_pdf('clinical_report.pdf')
print(f"Extracted Drug: {result['extractedData']['drugName']}")
print(f"Severity: {result['extractedData']['severity']}")
print(f"Classification: {result['classification']['causalityAssessment']['fullResponse']}")
```

### JavaScript/Node.js Example
```javascript
const fs = require('fs');
const axios = require('axios');

async function classifyEmail(subject, body, sender) {
    const response = await axios.post(
        'http://localhost:8080/api/mcp/tools/classify_from_email',
        {
            subject: subject,
            body: body,
            sender: sender
        }
    );
    
    return response.data;
}

// Usage
classifyEmail(
    'Adverse Event Report',
    'Patient developed rash after taking penicillin...',
    'nurse@hospital.com'
).then(result => {
    console.log('Extracted Data:', result.extractedData);
    console.log('Risk Level:', result.classification.riskAnalysis.insights);
});
```

## Error Handling

If extraction fails or required fields are missing:

```json
{
  "success": true,
  "extractedData": {
    "extractionError": "Failed to parse AI response",
    "isValid": false,
    "validationError": "Missing required fields: drugName and adverseEventDescription"
  },
  "classification": null,
  "message": "Extraction completed but validation failed"
}
```

## Configuration

### Environment Variables

Add to your `.env` or `application.yml`:

```yaml
openai:
  api-key: ${OPENAI_API_KEY}
  model: gpt-3.5-turbo  # or gpt-4 for better accuracy
```

### Model Selection

- **gpt-3.5-turbo**: Faster, lower cost, good accuracy
- **gpt-4**: Slower, higher cost, excellent accuracy
- **gpt-4-turbo**: Balanced performance

## Best Practices

1. **Document Quality**: Higher quality documents yield better extraction results
2. **Text Clarity**: Clear, structured text improves AI extraction accuracy
3. **Validation**: Always validate extracted data before using in production
4. **Error Handling**: Implement retry logic for API failures
5. **Rate Limiting**: OpenAI API has rate limits - implement queuing for batch processing
6. **Data Privacy**: Ensure PHI/PII compliance when sending data to OpenAI

## Testing

### Get Available Tools
```bash
curl http://localhost:8080/api/mcp/tools
```

### Test with Sample Data
```bash
# Test with telephony transcript
curl -X POST http://localhost:8080/api/mcp/tools/classify_from_telephony_transcript \
  -H "Content-Type: application/json" \
  -d '{
    "transcript": "Patient called to report nausea and vomiting after taking metformin. Symptoms started within 1 hour of taking the medication.",
    "callerInfo": "Patient John Doe"
  }'
```

## Troubleshooting

### Common Issues

1. **"Failed to parse PDF"**
   - Ensure PDF is not encrypted or password-protected
   - Check PDF is not corrupted
   - Verify Base64 encoding is correct

2. **"Missing required fields"**
   - Document may not contain clear adverse event information
   - Try providing more context in the document
   - Check OpenAI model selection (gpt-4 is more accurate)

3. **"OpenAI API error"**
   - Check API key is valid
   - Verify API quota/rate limits
   - Check network connectivity

## Performance Considerations

- **PDF Parsing**: ~1-2 seconds for typical documents
- **OpenAI Extraction**: ~3-5 seconds depending on text length
- **OpenAI Classification**: ~3-5 seconds per analysis (causality + risk)
- **Total Processing Time**: ~10-15 seconds per document

## License & Compliance

- Ensure compliance with local healthcare regulations (HIPAA, GDPR, etc.)
- OpenAI data processing policies apply
- Consider using Azure OpenAI Service for enhanced compliance

## Support

For issues or questions, please refer to the main project README or open an issue.

