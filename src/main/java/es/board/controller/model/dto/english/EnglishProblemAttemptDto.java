package es.board.controller.model.dto.english;

import es.board.infrastructure.english.entity.EnglishProblemAttempt;
import lombok.*;

import java.time.LocalDateTime;

public class EnglishProblemAttemptDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Request {
        private String userId;
        private String objectId;
        private String chosenAnswer;
        private Boolean isCorrect;
        private String category;
        private Integer part;
        private EnglishProblemAttempt.Level level;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String userId;
        private String objectId;
        private String chosenAnswer;
        private Boolean isCorrect;
        private String category;
        private Integer part;
        private EnglishProblemAttempt.Level level;
        private LocalDateTime createdAt;

        public static Response fromEntity(EnglishProblemAttempt e) {
            return Response.builder()
                    .id(e.getId())
                    .userId(e.getUserId())
                    .objectId(e.getObjectId())
                    .chosenAnswer(e.getChosenAnswer())
                    .isCorrect(e.getIsCorrect())
                    .category(e.getCategory())
                    .part(e.getPart())
                    .level(e.getLevel())
                    .createdAt(e.getCreatedAt())
                    .build();
        }
    }
}