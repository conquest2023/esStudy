package es.board.controller.model.dto.english;


import es.board.infrastructure.english.entity.EnglishProblemAttempt;
import es.board.infrastructure.english.entity.WrongNote;
import lombok.*;

import java.time.LocalDateTime;

public class WrongNoteDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        private String objectId;
        private String category;
        private Integer part;
        private EnglishProblemAttempt.Level level;

//        public WrongNote toEntity() {
//            return WrongNote.builder()
//                    .userId(userId)
//                    .objectId(objectId)
//                    .category(category)
//                    .part(part)
//                    .level(level)
//                    .wrongCount(1)
//                    .resolved(false)
//                    .resolvedAt(null)
//                    .build();
//        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String userId;
        private String objectId;
        private String category;
        private Integer part;
        private EnglishProblemAttempt.Level level;

        private Integer wrongCount;
        private LocalDateTime firstWrongAt;
        private LocalDateTime lastWrongAt;

        private Boolean resolved;
        private LocalDateTime resolvedAt;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(WrongNote e) {
            return Response.builder()
                    .id(e.getId())
                    .userId(e.getUserId())
                    .objectId(e.getObjectId())
                    .category(e.getCategory())
                    .part(e.getPart())
                    .level(e.getLevel())
                    .wrongCount(e.getWrongCount())
                    .firstWrongAt(e.getFirstWrongAt())
                    .lastWrongAt(e.getLastWrongAt())
                    .resolved(e.getResolved())
                    .resolvedAt(e.getResolvedAt())
                    .createdAt(e.getCreatedAt())
                    .updatedAt(e.getUpdatedAt())
                    .build();
        }
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResolveRequest {
        private String userId;
        private String objectId;
        private Boolean resolved; // true/false 토글
    }
}
