package es.board.controller.model.dto.poll;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.filter.annotation.XssSafeDeserializer;
import es.board.filter.annotation.XssSafeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PollOptionDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        private long optionId;

        @JsonSerialize(using = XssSafeSerializer.class)
        @JsonDeserialize(using = XssSafeDeserializer.class)
        private String content;

        private int sortOrder;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

//        private Long id;

        private Long pollId;

        @JsonSerialize(using = XssSafeSerializer.class)
        @JsonDeserialize(using = XssSafeDeserializer.class)
        private String content;

        private int sortOrder;

        private long voteCount;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime createdAt;
    }
}
