package es.board.infrastructure.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.gemini.GeminiRequest;
import es.board.controller.model.gemini.GeminiResponse;
import es.board.controller.model.gemini.RecommendationResponse;
import es.board.infrastructure.es.document.View;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;


    public List<String> getAnalysis(List<View> view) {
        String urlWithKey = apiUrl + "?key=" + apiKey;
        Map<String,String> map = new HashMap<>();
        for (View view1 : view) {
            map.put(view1.getTitle(), view1.getContent());
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("- 제목: ").append(entry.getKey()).append("\n");
            sb.append("  내용 일부: ").append(entry.getValue().substring(0, Math.min(entry.getValue().length(), 200))).append("\n\n");
        }
        String prompt = "사용자의 다음 열람 기록을 분석하여, 이 사람이 좋아할 만한 유튜브 '검색 키워드' 3개를 정확히 뽑아줘.\n" +
                "열람 기록: " + sb.toString() + "\n" +
                "응답 형식은 반드시 다음 JSON 구조로만 출력해줘:\n" +
                "{ \"recommendations\": [ { \"keyword\": \"키워드\", \"reason\": \"이유\" } ] }";

        GeminiRequest request = new GeminiRequest(prompt);

        GeminiResponse response = restTemplate.postForObject(urlWithKey, request, GeminiResponse.class);

        String jsonText = response.getText();

        if (jsonText.startsWith("```")) {
            jsonText = jsonText.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
        }
        RecommendationResponse recs = null;
        try {
            recs = objectMapper.<RecommendationResponse>readValue(jsonText, RecommendationResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        List<String> finalLinks = new ArrayList<>();
        for (RecommendationResponse.Recommendation rec : recs.getRecommendations()) {
            String encodedKeyword = rec.getKeyword();
            String youtubeUrl = "https://www.youtube.com/results?search_query=" + encodedKeyword;
            finalLinks.add("추천 키워드: " + rec.getKeyword() + "\n링크: " + youtubeUrl + "\n이유: " + rec.getReason());
        }
        return finalLinks;
    }
}