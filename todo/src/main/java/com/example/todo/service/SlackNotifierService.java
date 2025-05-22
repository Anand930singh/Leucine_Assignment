package com.example.todo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SlackNotifierService {

    private final String slackWebhookUrl;
    private final RestTemplate restTemplate;

    public SlackNotifierService(@Value("${slack.webhook.url}") String slackWebhookUrl) {
        this.slackWebhookUrl = slackWebhookUrl;
        this.restTemplate = new RestTemplate();
    }

    public void sendToSlack(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = Map.of("text", message);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        restTemplate.postForEntity(slackWebhookUrl, request, String.class);
    }
}
