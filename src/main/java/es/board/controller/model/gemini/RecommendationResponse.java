package es.board.controller.model.gemini;

import lombok.Data;

import java.util.List;
@Data
public class RecommendationResponse {
    private List<Recommendation> recommendations;

    @Data
    public static class Recommendation {
        private String keyword;
        private String reason;
        // Getter, Setter
    }
}