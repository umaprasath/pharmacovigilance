package com.pharmacovigilance.mcpagent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/docs")
@Tag(name = "API Documentation", description = "Comprehensive API documentation and examples")
public class SwaggerDocumentationController {

    @GetMapping("/examples")
    @Operation(summary = "Get API examples", description = "Retrieve comprehensive examples for all API endpoints")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved API examples",
            content = @Content(mediaType = "application/json",
                    examples = {
                        @ExampleObject(name = "Create Adverse Event", value = """
                        {
                          "caseNumber": "AE-2024-001",
                          "patientId": "P001",
                          "drugName": "Aspirin",
                          "adverseEventDescription": "Severe headache and nausea after taking aspirin",
                          "severity": "MODERATE",
                          "status": "NEW",
                          "symptoms": "Severe headache, nausea, dizziness",
                          "medicalHistory": "Hypertension, Diabetes Type 2",
                          "concomitantMedications": "Metformin, Lisinopril",
                          "reporterNotes": "Patient reported symptoms 2 hours after taking aspirin",
                          "eventDate": "2024-01-15T10:30:00",
                          "reportDate": "2024-01-15T12:00:00"
                        }
                        """),
                        @ExampleObject(name = "Create Patient", value = """
                        {
                          "patientId": "P001",
                          "firstName": "John",
                          "lastName": "Doe",
                          "dateOfBirth": "1980-05-15",
                          "gender": "MALE",
                          "weight": 75.5,
                          "height": 175.0,
                          "medicalHistory": "Hypertension, Diabetes Type 2",
                          "allergies": "Penicillin",
                          "currentMedications": "Metformin, Lisinopril",
                          "contactInfo": "john.doe@email.com"
                        }
                        """),
                        @ExampleObject(name = "Create Drug", value = """
                        {
                          "drugCode": "ASP001",
                          "drugName": "Aspirin",
                          "genericName": "Acetylsalicylic Acid",
                          "manufacturer": "Bayer",
                          "activeIngredient": "Acetylsalicylic Acid",
                          "indications": "Pain relief, Anti-inflammatory, Anti-platelet",
                          "contraindications": "Active bleeding, Peptic ulcer, Severe liver disease",
                          "knownAdverseEffects": "Gastrointestinal bleeding, Nausea, Vomiting, Headache",
                          "dosageForm": "Tablet",
                          "strength": "325mg",
                          "status": "ACTIVE"
                        }
                        """),
                        @ExampleObject(name = "MCP Tool - Get Adverse Events", value = """
                        {
                          "severity": "SEVERE",
                          "status": "NEW",
                          "drugName": "Aspirin"
                        }
                        """),
                        @ExampleObject(name = "MCP Tool - Get Statistics", value = """
                        {}
                        """)
                    }))
    public ResponseEntity<Map<String, Object>> getApiExamples() {
        Map<String, Object> examples = new java.util.HashMap<>();
        
        Map<String, Object> adverseEventExample = new java.util.HashMap<>();
        adverseEventExample.put("caseNumber", "AE-2024-001");
        adverseEventExample.put("patientId", "P001");
        adverseEventExample.put("drugName", "Aspirin");
        adverseEventExample.put("adverseEventDescription", "Severe headache and nausea after taking aspirin");
        adverseEventExample.put("severity", "MODERATE");
        adverseEventExample.put("status", "NEW");
        adverseEventExample.put("symptoms", "Severe headache, nausea, dizziness");
        adverseEventExample.put("medicalHistory", "Hypertension, Diabetes Type 2");
        adverseEventExample.put("concomitantMedications", "Metformin, Lisinopril");
        adverseEventExample.put("reporterNotes", "Patient reported symptoms 2 hours after taking aspirin");
        adverseEventExample.put("eventDate", "2024-01-15T10:30:00");
        adverseEventExample.put("reportDate", "2024-01-15T12:00:00");
        
        Map<String, Object> patientExample = new java.util.HashMap<>();
        patientExample.put("patientId", "P001");
        patientExample.put("firstName", "John");
        patientExample.put("lastName", "Doe");
        patientExample.put("dateOfBirth", "1980-05-15");
        patientExample.put("gender", "MALE");
        patientExample.put("weight", 75.5);
        patientExample.put("height", 175.0);
        patientExample.put("medicalHistory", "Hypertension, Diabetes Type 2");
        patientExample.put("allergies", "Penicillin");
        patientExample.put("currentMedications", "Metformin, Lisinopril");
        patientExample.put("contactInfo", "john.doe@email.com");
        
        Map<String, Object> drugExample = new java.util.HashMap<>();
        drugExample.put("drugCode", "ASP001");
        drugExample.put("drugName", "Aspirin");
        drugExample.put("genericName", "Acetylsalicylic Acid");
        drugExample.put("manufacturer", "Bayer");
        drugExample.put("activeIngredient", "Acetylsalicylic Acid");
        drugExample.put("indications", "Pain relief, Anti-inflammatory, Anti-platelet");
        drugExample.put("contraindications", "Active bleeding, Peptic ulcer, Severe liver disease");
        drugExample.put("knownAdverseEffects", "Gastrointestinal bleeding, Nausea, Vomiting, Headache");
        drugExample.put("dosageForm", "Tablet");
        drugExample.put("strength", "325mg");
        drugExample.put("status", "ACTIVE");
        
        Map<String, Object> mcpToolExamples = new java.util.HashMap<>();
        mcpToolExamples.put("getAdverseEvents", Map.of("severity", "SEVERE", "status", "NEW", "drugName", "Aspirin"));
        mcpToolExamples.put("getPatientInfo", Map.of("patientId", "P001"));
        mcpToolExamples.put("getDrugInfo", Map.of("drugCode", "ASP001"));
        mcpToolExamples.put("createAdverseEvent", Map.of("caseNumber", "AE-2024-002", "patientId", "P002", "drugName", "Metformin", "adverseEventDescription", "Gastrointestinal upset", "severity", "MILD", "status", "NEW"));
        mcpToolExamples.put("getStatistics", Map.of());
        
        examples.put("adverseEventExample", adverseEventExample);
        examples.put("patientExample", patientExample);
        examples.put("drugExample", drugExample);
        examples.put("mcpToolExamples", mcpToolExamples);
        
        return ResponseEntity.ok(examples);
    }

    @GetMapping("/endpoints")
    @Operation(summary = "Get API endpoints summary", description = "Retrieve a summary of all available API endpoints")
    public ResponseEntity<Map<String, Object>> getApiEndpoints() {
        Map<String, Object> endpoints = new java.util.HashMap<>();
        
        Map<String, Object> adverseEventsMethods = new java.util.HashMap<>();
        adverseEventsMethods.put("GET", "Get all adverse events (paginated)");
        adverseEventsMethods.put("POST", "Create new adverse event");
        adverseEventsMethods.put("GET /{id}", "Get adverse event by ID");
        adverseEventsMethods.put("PUT /{id}", "Update adverse event");
        adverseEventsMethods.put("DELETE /{id}", "Delete adverse event");
        adverseEventsMethods.put("GET /search", "Search adverse events");
        adverseEventsMethods.put("GET /statistics", "Get statistics");
        adverseEventsMethods.put("PATCH /{id}/status", "Update event status");
        
        Map<String, Object> adverseEvents = new java.util.HashMap<>();
        adverseEvents.put("baseUrl", "/api/adverse-events");
        adverseEvents.put("methods", adverseEventsMethods);
        
        Map<String, Object> patientsMethods = new java.util.HashMap<>();
        patientsMethods.put("GET", "Get all patients (paginated)");
        patientsMethods.put("POST", "Create new patient");
        patientsMethods.put("GET /{id}", "Get patient by ID");
        patientsMethods.put("GET /patient-id/{patientId}", "Get patient by patient ID");
        patientsMethods.put("PUT /{id}", "Update patient");
        patientsMethods.put("DELETE /{id}", "Delete patient");
        patientsMethods.put("GET /search", "Search patients by last name");
        
        Map<String, Object> patients = new java.util.HashMap<>();
        patients.put("baseUrl", "/api/patients");
        patients.put("methods", patientsMethods);
        
        Map<String, Object> drugsMethods = new java.util.HashMap<>();
        drugsMethods.put("GET", "Get all drugs (paginated)");
        drugsMethods.put("POST", "Create new drug");
        drugsMethods.put("GET /{id}", "Get drug by ID");
        drugsMethods.put("GET /drug-code/{drugCode}", "Get drug by drug code");
        drugsMethods.put("PUT /{id}", "Update drug");
        drugsMethods.put("DELETE /{id}", "Delete drug");
        drugsMethods.put("GET /search", "Search drugs by name");
        drugsMethods.put("GET /manufacturer/{manufacturer}", "Get drugs by manufacturer");
        
        Map<String, Object> drugs = new java.util.HashMap<>();
        drugs.put("baseUrl", "/api/drugs");
        drugs.put("methods", drugsMethods);
        
        Map<String, Object> mcpMethods = new java.util.HashMap<>();
        mcpMethods.put("GET /tools", "Get available MCP tools");
        mcpMethods.put("POST /tools/{toolName}", "Execute MCP tool");
        
        Map<String, Object> availableTools = new java.util.HashMap<>();
        availableTools.put("get_adverse_events", "Retrieve adverse events by criteria");
        availableTools.put("get_patient_info", "Get patient information by ID");
        availableTools.put("get_drug_info", "Get drug information by code or name");
        availableTools.put("create_adverse_event", "Create a new adverse event report");
        availableTools.put("update_adverse_event_status", "Update adverse event status");
        availableTools.put("get_statistics", "Get pharmacovigilance statistics");
        
        Map<String, Object> mcpTools = new java.util.HashMap<>();
        mcpTools.put("baseUrl", "/api/mcp");
        mcpTools.put("methods", mcpMethods);
        mcpTools.put("availableTools", availableTools);
        
        Map<String, Object> swagger = new java.util.HashMap<>();
        swagger.put("ui", "/swagger-ui.html");
        swagger.put("apiDocs", "/api-docs");
        swagger.put("openApiJson", "/v3/api-docs");
        
        endpoints.put("adverseEvents", adverseEvents);
        endpoints.put("patients", patients);
        endpoints.put("drugs", drugs);
        endpoints.put("mcpTools", mcpTools);
        endpoints.put("swagger", swagger);
        
        return ResponseEntity.ok(endpoints);
    }
}
