package com.example.supporttemplate.controller;

import com.example.supporttemplate.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateTemplate(@RequestBody Map<String, String> request) {
        try {
            // Get the issue type from the request
            String issueType = request.get("issueType");

            // Validate the input
            if (issueType == null || issueType.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Issue type is required");
                return ResponseEntity.badRequest().body(error);
            }

            // Generate the template using Gemini
            String template = geminiService.generateTemplate(issueType);

            // Prepare and return the response
            Map<String, String> response = new HashMap<>();
            response.put("issueType", issueType);
            response.put("template", template);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle any errors
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate template: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is running!");
    }
}
