package es.board.controller.model.dto.feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.filter.annotation.XssSafeDeserializer;
import es.board.filter.annotation.XssSafeSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public class CommentDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Response {


        private Long id;

        private int postId;

        private String userId;

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

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class  Request{

        private long id;

        @JsonSerialize(using = XssSafeSerializer.class)
        private String username;


        private  boolean  owner;

        @JsonSerialize(using = XssSafeSerializer.class)
        private String content;

        private  int likeCount;

        private  boolean isAuthor;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class  Update{


        private long id;

        private String username;

        private String content;

        private LocalDateTime updatedAt;
    }

}
