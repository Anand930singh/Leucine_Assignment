package com.example.todo.service;

import com.example.todo.config.Status;
import com.example.todo.entity.Todo;
import com.example.todo.entity.Users;
import com.example.todo.repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LLMSummarizerService {

    private static final Logger logger = LoggerFactory.getLogger(LLMSummarizerService.class);
    private final ToDoRepository toDoRepository;
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;
    private final SlackNotifierService slackNotifierService;

    public LLMSummarizerService(
            ToDoRepository toDoRepository,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.url}") String apiUrl,
            SlackNotifierService slackNotifierService) {
        this.toDoRepository = toDoRepository;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.restTemplate = new RestTemplate();
        this.slackNotifierService = slackNotifierService;
    }

    @Transactional(readOnly = true)
    public Map<String, String> summarizePendingTasks(Users user) {
        Map<String, String> result = new HashMap<>();
        try {
            List<Todo> pendingTasks = toDoRepository.findByUserAndStatus(user, Status.PENDING);

            if (pendingTasks.isEmpty()) {
                logger.info("No pending tasks found for user: {}", user.getUsername());
                result.put("summary", "No pending tasks found.");
                result.put("slackStatus", "skipped");
                return result;
            }

            String prompt = createPrompt(pendingTasks);
            logger.debug("Generated prompt for user {}: {}", user.getUsername(), prompt);
            
            String summary = getGeminiSummary(prompt);
            logger.info("Generated summary for user {}: {}", user.getUsername(), summary);
            result.put("summary", summary);

            try {
                slackNotifierService.sendToSlack("*Summary for " + user.getUsername() + ":*\n" + summary);
                result.put("slackStatus", "success");
            } catch (Exception e) {
                logger.error("Failed to send summary to Slack: {}", e.getMessage());
                result.put("slackStatus", "failed");
            }

            return result;
        } catch (Exception e) {
            logger.error("Error generating summary for user {}: {}", user.getUsername(), e.getMessage(), e);
            result.put("summary", "Error generating summary: " + e.getMessage());
            result.put("slackStatus", "failed");
            return result;
        }
    }

    private String createPrompt(List<Todo> tasks) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please provide a concise summary of the following pending tasks:\n\n");
        
        for (int i = 0; i < tasks.size(); i++) {
            Todo task = tasks.get(i);
            prompt.append(i + 1).append(". ").append(task.getTask())
                  .append(" (Created: ").append(task.getCreatedAt()).append(")\n");
        }
        
        prompt.append("\nPlease summarize these tasks in a clear and organized way, highlighting any patterns or priorities.");
        
        return prompt.toString();
    }

    private String getGeminiSummary(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            contents.put("parts", List.of(Map.of("text", prompt)));
            requestBody.put("contents", List.of(contents));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            logger.debug("Sending request to Gemini API: {}", requestBody);
            Map<String, Object> response = restTemplate.postForObject(
                apiUrl,
                request,
                Map.class
            );
            logger.debug("Received response from Gemini API: {}", response);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            
            logger.warn("No summary generated from Gemini API response");
            return "No summary generated.";
        } catch (Exception e) {
            logger.error("Error getting summary from Gemini API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get summary from Gemini API: " + e.getMessage());
        }
    }
} 