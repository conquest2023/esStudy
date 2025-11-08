package es.board.controller.model.dto.feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.ex.annotation.ValidCategory;
import es.board.filter.XssSafeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class PostDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class Request {
        private int id;

        @JsonIgnore
        private String userId;

        private String feedUID;

        private boolean owner;

        @JsonSerialize(using = XssSafeSerializer.class)
        private String username;

        @JsonSerialize(using = XssSafeSerializer.class)
        private String imageURL;

        @JsonSerialize(using = XssSafeSerializer.class)
        private String title;

        //    @JsonSerialize(using = XssSafeSerializer.class)
        private String description;


        @JsonSerialize(using = XssSafeSerializer.class)
        private String category;


        private int likeCount;

        private int viewCount;


        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime createdAt;

    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private  int id;

        private  String userId;


        private String feedUID;


        @JsonSerialize(using = XssSafeSerializer.class)
        private String username;

        //    @JsonSerialize(using = XssSafeSerializer.class)
        private  String imageURL;


        @JsonSerialize(using = XssSafeSerializer.class)
        private String title;

        @ValidCategory
        @JsonSerialize(using = XssSafeSerializer.class)
        private  String category;

        //    @JsonSerialize(using = XssSafeSerializer.class)
        private String description;

        private  boolean anonymous;


        private int  viewCount;


        private  int likeCount;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime createdAt;

        @JsonIgnore
        @JsonSerialize(using = XssSafeSerializer.class)
        private MultipartFile attachFile;

        @JsonIgnore
        @JsonSerialize(using = XssSafeSerializer.class)
        private List<MultipartFile> imageFiles;


        public void TimePush(){

            this.createdAt=LocalDateTime.now();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class Update {

        private String feedUID;

        private String username;

        private String title;

        private String description;

        private  int likeCount;

        private LocalDate updatedAt;


    }

}
