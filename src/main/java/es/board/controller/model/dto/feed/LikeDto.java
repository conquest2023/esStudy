package es.board.controller.model.dto.feed;

import es.board.domain.enum_type.TargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

public class LikeDto {


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Request {

        private int postId;
//        private String userId;
        private Long targetId;
        private TargetType targetType;
        private LocalDateTime createdAt;
    }


    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Response {
        private long id;

        private boolean isOwner;
        private int postId;
        private Long targetId;
        private TargetType targetType;
    }
}
