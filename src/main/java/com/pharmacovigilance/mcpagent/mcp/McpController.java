package com.pharmacovigilance.mcpagent.mcp;

/**
 * McpController.java
 * This class is the controller for the MCP tools.
 * It is used to execute the MCP tools and return the results.
 * It is also used to get the available MCP tools and their parameters.
 * It is also used to get the available MCP tools and their parameters.
 */
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MCP Tools", description = "Model Context Protocol tools for external system integration")
public class McpController {
    
    private final McpServer mcpServer;
    
    @PostMapping("/tools/{toolName}")
    @Operation(summary = "Execute MCP tool", description = "Execute a specific MCP tool with the provided parameters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tool executed successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Invalid tool name or parameters")
    })
    public ResponseEntity<Map<String, Object>> executeTool(
            @Parameter(description = "Name of the MCP tool to execute", required = true)
            @PathVariable String toolName,
            @Parameter(description = "Tool parameters", required = true)
            @RequestBody Map<String, Object> params) {
        
        log.info("Executing MCP tool: {} with params: {}", toolName, params);
        
        Map<String, Object> result = switch (toolName) {
            case "get_adverse_events" -> mcpServer.getAdverseEvents(params);
            case "get_patient_info" -> mcpServer.getPatientInfo(params);
            case "get_drug_info" -> mcpServer.getDrugInfo(params);
            case "create_adverse_event" -> mcpServer.createAdverseEvent(params);
            case "update_adverse_event_status" -> mcpServer.updateAdverseEventStatus(params);
            case "get_statistics" -> mcpServer.getStatistics(params);
            case "classify_adverse_event" -> mcpServer.classifyAdverseEvent(params);
            case "classify_event_from_input" -> mcpServer.classifyEventFromInput(params);
            case "classify_from_pdf" -> mcpServer.classifyFromPdf(params);
            case "classify_from_email" -> mcpServer.classifyFromEmail(params);
            case "classify_from_telephony_transcript" -> mcpServer.classifyFromTelephonyTranscript(params);
            case "classify_from_document" -> mcpServer.classifyFromDocument(params);
            default -> {
                Map<String, Object> error = Map.of(
                    "success", false,
                    "error", "Unknown tool: " + toolName
                );
                yield error;
            }
        };
        
        boolean success = (Boolean) result.get("success");
        return success ? 
            ResponseEntity.ok(result) : 
            ResponseEntity.badRequest().body(result);
    }
    
    @GetMapping("/tools")
    @Operation(summary = "Get available MCP tools", description = "Retrieve a list of all available MCP tools and their parameters")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved available tools",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> getAvailableTools() {
        Map<String, Object> toolsMap = new HashMap<>();
        
        toolsMap.put("get_adverse_events", Map.of(
            "description", "Retrieve adverse events by criteria",
            "parameters", Map.of(
                "severity", "string (optional)",
                "status", "string (optional)",
                "drugName", "string (optional)",
                "patientId", "string (optional)"
            )
        ));
        
        toolsMap.put("get_patient_info", Map.of(
            "description", "Get patient information by ID",
            "parameters", Map.of(
                "patientId", "string (required)"
            )
        ));
        
        toolsMap.put("get_drug_info", Map.of(
            "description", "Get drug information by code or name",
            "parameters", Map.of(
                "drugCode", "string (optional)",
                "drugName", "string (optional)"
            )
        ));
        
        toolsMap.put("create_adverse_event", Map.of(
            "description", "Create a new adverse event report",
            "parameters", Map.of(
                "caseNumber", "string (required)",
                "patientId", "string (required)",
                "drugName", "string (required)",
                "adverseEventDescription", "string (required)",
                "severity", "string (required)",
                "status", "string (required)"
            )
        ));
        
        toolsMap.put("update_adverse_event_status", Map.of(
            "description", "Update adverse event status",
            "parameters", Map.of(
                "eventId", "long (required)",
                "status", "string (required)"
            )
        ));
        
        toolsMap.put("get_statistics", Map.of(
            "description", "Get pharmacovigilance statistics",
            "parameters", Map.of()
        ));
        
        toolsMap.put("classify_adverse_event", Map.of(
            "description", "Classify an existing adverse event using AI (causality assessment and risk analysis)",
            "parameters", Map.of(
                "eventId", "long (required) - ID of the adverse event to classify"
            )
        ));
        
        Map<String, String> classifyInputParams = new HashMap<>();
        classifyInputParams.put("drugName", "string (required) - Name of the drug");
        classifyInputParams.put("adverseEventDescription", "string (required) - Description of the adverse event");
        classifyInputParams.put("severity", "string (optional) - Severity level (MILD, MODERATE, SEVERE, LIFE_THREATENING, FATAL)");
        classifyInputParams.put("symptoms", "string (optional) - Symptoms description");
        classifyInputParams.put("medicalHistory", "string (optional) - Patient medical history");
        classifyInputParams.put("concomitantMedications", "string (optional) - Other medications being taken");
        classifyInputParams.put("patientId", "string (optional) - Patient ID for additional context");
        classifyInputParams.put("caseNumber", "string (optional) - Case number for reference");
        
        toolsMap.put("classify_event_from_input", Map.of(
            "description", "Classify event from raw input data using AI without creating a database record",
            "parameters", classifyInputParams
        ));
        
        toolsMap.put("classify_from_pdf", Map.of(
            "description", "Extract and classify adverse event from PDF clinical document using AI",
            "parameters", Map.of(
                "pdfContent", "string (required) - Base64 encoded PDF content",
                "fileName", "string (optional) - Name of the PDF file for reference"
            )
        ));
        
        Map<String, String> emailParams = new HashMap<>();
        emailParams.put("subject", "string (optional) - Email subject (required if emlContent not provided)");
        emailParams.put("body", "string (optional) - Email body (required if emlContent not provided)");
        emailParams.put("sender", "string (optional) - Email sender information");
        emailParams.put("emlContent", "string (optional) - Base64 encoded .eml file content");
        
        toolsMap.put("classify_from_email", Map.of(
            "description", "Extract and classify adverse event from email content using AI",
            "parameters", emailParams
        ));
        
        toolsMap.put("classify_from_telephony_transcript", Map.of(
            "description", "Extract and classify adverse event from phone call transcript using AI",
            "parameters", Map.of(
                "transcript", "string (required) - Telephony call transcript text",
                "callerInfo", "string (optional) - Information about the caller",
                "callId", "string (optional) - Call ID for reference"
            )
        ));
        
        toolsMap.put("classify_from_document", Map.of(
            "description", "Extract and classify adverse event from any document format (PDF, Word, text, etc.) using AI",
            "parameters", Map.of(
                "documentContent", "string (required) - Base64 encoded document content",
                "fileName", "string (optional) - Name of the document for reference",
                "documentType", "string (optional) - Document type (auto-detected if not provided)"
            )
        ));
        
        return ResponseEntity.ok(Map.of("tools", toolsMap));
    }
}
