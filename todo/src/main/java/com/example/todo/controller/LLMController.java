package com.example.todo.controller;

import com.example.todo.dto.ResponseDto;
import com.example.todo.service.LLMSummarizerService;
import com.example.todo.utils.JWTUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/llm")
public class LLMController {

    private final LLMSummarizerService llmSummarizerService;
    private final JWTUtils jwtUtils;

    public LLMController(LLMSummarizerService llmSummarizerService, JWTUtils jwtUtils) {
        this.llmSummarizerService = llmSummarizerService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/summarize-tasks")
    public ResponseEntity<ResponseDto<Object>> summarizeTasks(
            @RequestHeader("Authorization") String token
    ) {
        try {
            String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            var userOptional = jwtUtils.getUsernameFromToken(rawToken);
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(new ResponseDto<>(404, "User not found"));
            }

            Map<String, String> result = llmSummarizerService.summarizePendingTasks(userOptional.get());
            return ResponseEntity.ok(new ResponseDto<>(200, "Summary generated successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ResponseDto<>(500, "Error generating summary: " + e.getMessage()));
        }
    }
} 