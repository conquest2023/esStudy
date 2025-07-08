package es.board.controller.model.dto.feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;



public class VoteDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public  static class Request {

        private Long id;

        private Long voteId;

        private String feedUID;

        private Long postId;

        private String userId;

        private String username;


        private String title;

        private String selectedOption;

        private String description;

        private String category;

        private List<String> voteType;

        private Boolean upvote;


        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime createdAt;

    }
}