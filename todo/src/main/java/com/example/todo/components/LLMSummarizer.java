//package com.example.todo.components;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import okhttp3.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class LLMSummarizer {
//
//        @Value("${gemini.api.key}")
//        private String apiKey;
//
//        @Value("${gemini.api.url}")
//        private String apiUrl;
//
//        private final OkHttpClient client = new OkHttpClient();
//        private final ObjectMapper mapper = new ObjectMapper();
//
//        public String summarizeText(String text) throws IOException {
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("contents", List.of(
//                    Map.of("parts", List.of(Map.of("text", "Summarize this:\n" + text)))
//            ));
//
//            RequestBody body = RequestBody.create(
//                    mapper.writeValueAsString(requestBody),
//                    MediaType.get("application/json")
//            );
//
//            HttpUrl url = HttpUrl.parse(apiUrl).newBuilder()
//                    .addQueryParameter("key", apiKey)
//                    .build();
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .post((okhttp3.RequestBody) body)
//                    .build();
//
//            try (Response response = client.newCall(request).execute()) {
//                if (!response.isSuccessful()) throw new IOException("Unexpected code: " + response);
//
//                Map responseMap = mapper.readValue(response.body().string(), Map.class);
//                List choices = (List) responseMap.get("candidates");
//                if (choices != null && !choices.isEmpty()) {
//                    Map firstChoice = (Map) choices.get(0);
//                    Map content = (Map) firstChoice.get("content");
//                    List parts = (List) content.get("parts");
//                    return (String) ((Map) parts.get(0)).get("text");
//                }
//            }
//
//            return "No summary generated.";
//        }
//
//}
