package es.board.controller.model.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateResponse {

    private String commentUID;

    private  String feedUID;

    private String username;


    private String content;

    private  int likeCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;


    public void TimeNow() {
        this.createdAt=LocalDateTime.now();
    }

    public void commentBasicSetting(String id) {
        this.feedUID=id;
        this.createdAt=LocalDateTime.now();
        this.commentUID=UUID.randomUUID().toString();
    }
}
