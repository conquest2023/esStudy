package es.board.controller.model.dto.feed;

import com.fasterxml.jackson.annotation.JsonFormat;
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


public class ReplyDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class Response {

    private String id;

    private String commentUID;

    private  String  userId;

    private String feedUID;
    @JsonSerialize(using = XssSafeSerializer.class)
    @JsonDeserialize(using = XssSafeDeserializer.class)
    private String username;

    @JsonSerialize(using = XssSafeSerializer.class)
    @JsonDeserialize(using = XssSafeDeserializer.class)
    @NotBlank(message = "내용은 필수입니다.")
    private String content;


    private int likeCount;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime  createdAt;

    public void TimePush(){
        this.createdAt=LocalDateTime.now();
    }


    public void replyBasicSetting(String username, String userId) {
        this.username=username;
        this.userId=userId;
        this.createdAt=LocalDateTime.now();
    }

    public void replyAnonymousBasicSetting() {
        this.userId="";
        this.username="익명";
        this.createdAt=LocalDateTime.now();
        }
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class Request {

        private String id;

        private String commentUID;

        private String feedUID;

        private String username;

        @JsonSerialize(using = XssSafeSerializer.class)
        private String content;

        private String category;

        private int likeCount;


        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime createdAt;

    }
}
