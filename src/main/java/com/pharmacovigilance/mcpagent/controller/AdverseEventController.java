package com.pharmacovigilance.mcpagent.controller;

import com.pharmacovigilance.mcpagent.agent.PharmacovigilanceAgent;
import com.pharmacovigilance.mcpagent.model.AdverseEvent;
import com.pharmacovigilance.mcpagent.service.AdverseEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/adverse-events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Adverse Events", description = "API endpoints for managing adverse drug events")
public class AdverseEventController {
    
    private final AdverseEventService adverseEventService;
    private final PharmacovigilanceAgent pharmacovigilanceAgent;
    
    @GetMapping
    @Operation(summary = "Get all adverse events", description = "Retrieve a paginated list of all adverse drug events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved adverse events",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public ResponseEntity<Page<AdverseEvent>> getAllAdverseEvents(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.info("Fetching all adverse events with pagination");
        Page<AdverseEvent> events = adverseEventService.findAll(pageable);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get adverse event by ID", description = "Retrieve a specific adverse event by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved adverse event",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AdverseEvent.class))),
        @ApiResponse(responseCode = "404", description = "Adverse event not found")
    })
    public ResponseEntity<AdverseEvent> getAdverseEventById(
            @Parameter(description = "Adverse event ID", required = true) @PathVariable Long id) {
        log.info("Fetching adverse event with id: {}", id);
        Optional<AdverseEvent> event = adverseEventService.findById(id);
        return event.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create new adverse event", description = "Create a new adverse drug event report and trigger automated processing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Adverse event created successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AdverseEvent.class),
                        examples = @ExampleObject(value = """
                        {
                          "caseNumber": "AE-2024-001",
                          "patientId": "P001",
                          "drugName": "Aspirin",
                          "adverseEventDescription": "Severe headache and nausea",
                          "severity": "MODERATE",
                          "status": "NEW",
                          "symptoms": "Headache, nausea, dizziness",
                          "eventDate": "2024-01-15T10:30:00"
                        }
                        """))),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<AdverseEvent> createAdverseEvent(
            @Parameter(description = "Adverse event data", required = true)
            @Valid @RequestBody AdverseEvent adverseEvent) {
        log.info("Creating new adverse event: {}", adverseEvent.getCaseNumber());
        AdverseEvent savedEvent = adverseEventService.save(adverseEvent);
        
        // Trigger automated processing
        pharmacovigilanceAgent.processNewAdverseEvent(savedEvent.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AdverseEvent> updateAdverseEvent(@PathVariable Long id, 
                                                         @Valid @RequestBody AdverseEvent adverseEvent) {
        log.info("Updating adverse event with id: {}", id);
        Optional<AdverseEvent> existingEvent = adverseEventService.findById(id);
        
        if (existingEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        adverseEvent.setId(id);
        AdverseEvent updatedEvent = adverseEventService.save(adverseEvent);
        return ResponseEntity.ok(updatedEvent);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdverseEvent(@PathVariable Long id) {
        log.info("Deleting adverse event with id: {}", id);
        Optional<AdverseEvent> event = adverseEventService.findById(id);
        
        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        adverseEventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<AdverseEvent>> searchAdverseEvents(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String drugName,
            @RequestParam(required = false) String patientId) {
        
        log.info("Searching adverse events with criteria - severity: {}, status: {}, drugName: {}, patientId: {}", 
                severity, status, drugName, patientId);
        
        List<AdverseEvent> events = adverseEventService.findByCriteria(severity, status, drugName, patientId);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("Fetching adverse event statistics");
        
        Map<String, Object> stats = Map.of(
            "totalEvents", adverseEventService.count(),
            "severityCounts", adverseEventService.getCountBySeverity(),
            "statusCounts", adverseEventService.getCountByStatus()
        );
        
        return ResponseEntity.ok(stats);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<AdverseEvent> updateStatus(@PathVariable Long id, 
                                                   @RequestParam String status) {
        log.info("Updating status for adverse event {} to {}", id, status);
        
        Optional<AdverseEvent> eventOpt = adverseEventService.findById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        AdverseEvent event = eventOpt.get();
        try {
            event.setStatus(AdverseEvent.EventStatus.valueOf(status));
            AdverseEvent updatedEvent = adverseEventService.save(event);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
