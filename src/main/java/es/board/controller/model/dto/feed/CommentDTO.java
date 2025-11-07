package es.board.controller.model.dto.feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.filter.XssSafeDeserializer;
import es.board.filter.XssSafeSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


public class CommentDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Response {


        private Long id;

        private int postId;

        private String commentUID;

        private String userId;

        private String feedUID;

        @JsonSerialize(using = XssSafeSerializer.class)
        @JsonDeserialize(using = XssSafeDeserializer.class)
        private String username;

        @JsonSerialize(using = XssSafeSerializer.class)
        @JsonDeserialize(using = XssSafeDeserializer.class)
        @NotBlank(message = "내용은 필수입니다.")
        private String content;


        private int likeCount;

        private boolean anonymous;


        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime createdAt;


        public void TimeNow() {
            this.createdAt = LocalDateTime.now();
        }

        public void commentBasicSetting(String id, String username, String userId) {
            this.feedUID = id;
            this.username = username;
            this.userId = userId;
            this.createdAt = LocalDateTime.now();
            this.commentUID = UUID.randomUUID().toString();
        }

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class  Request{

        private long id;

        private String commentUID;

        private String feedUID;

        @JsonSerialize(using = XssSafeSerializer.class)
        private String username;

        @JsonIgnore
        private  String userId;

        @JsonSerialize(using = XssSafeSerializer.class)
        private String content;

        private  int likeCount;

        private  boolean isAuthor;

        private  boolean  isCommentOwner;

        private  boolean anonymous;


        private LocalDateTime createdAt;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class  Update{

        private String CommentUID;

        private  String feedUID;

        private String username;

        private String content;

        private  int LikeCount;

        private LocalDateTime updatedAt;
    }

}
