package es.board.controller.model.dto.poll;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.filter.XssSafeDeserializer;
import es.board.filter.XssSafeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PollVoteDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long voteId;

        private Long pollId;

        private Long optionId;

        @JsonSerialize(using = XssSafeSerializer.class)
        @JsonDeserialize(using = XssSafeDeserializer.class)
        private String userId;


        private LocalDateTime createdAt;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {


        private Long pollId;

        private Long optionId;

        @JsonSerialize(using = XssSafeSerializer.class)
        @JsonDeserialize(using = XssSafeDeserializer.class)
        private String voterId;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime votedAt;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MultiRequest {


        private Long pollId;

        private List<Long> optionIds;

        @JsonSerialize(using = XssSafeSerializer.class)
        @JsonDeserialize(using = XssSafeDeserializer.class)
        private String voterId;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime votedAt;
    }

}
