package es.board.controller.model.dto.feed;

import es.board.domain.enum_type.TargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class LikeDto {


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Response {

        private int postId;
//        private String userId;
        private Long targetId;
        private TargetType targetType;
        private LocalDateTime createdAt;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Request{
        private long id;

        private boolean isOwner;
        private int postId;
        private Long targetId;
        private TargetType targetType;
    }
}
