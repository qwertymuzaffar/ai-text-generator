package com.example.supporttemplate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateTemplate(String issueType) {
        try {
            // Create the prompt for Gemini
            String prompt = String.format(
                    "Create a professional customer support response template for the following issue type: '%s'. " +
                            "Include placeholders like [CUSTOMER_NAME], [TICKET_NUMBER], [SPECIFIC_DETAILS] where appropriate. " +
                            "Make it friendly, professional, and helpful. Keep it concise but comprehensive.",
                    issueType
            );

            // Build the request body using nested Maps
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> part = new HashMap<>();
            part.put("parts", List.of(textPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(part));

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the request entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Make the API call
            String urlWithKey = apiUrl + "?key=" + apiKey;
            String response = restTemplate.postForObject(urlWithKey, requestEntity, String.class);

            // Parse the response
            JsonNode jsonResponse = objectMapper.readTree(response);

            // Extract the generated text from the response
            return jsonResponse
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate template: " + e.getMessage(), e);
        }
    }
}
