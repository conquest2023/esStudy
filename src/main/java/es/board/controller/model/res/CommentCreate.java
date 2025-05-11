package es.board.controller.model.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.filter.XssSafeDeserializer;
import es.board.filter.XssSafeSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreate {

    private String commentUID;

    private  String userId;

    private  String feedUID;

    @JsonSerialize(using = XssSafeSerializer.class)
    @JsonDeserialize(using = XssSafeDeserializer.class)
    private String username;

    @JsonSerialize(using = XssSafeSerializer.class)
    @JsonDeserialize(using = XssSafeDeserializer.class)
    @NotBlank(message = "내용은 필수입니다.")
    private String content;


    private  int likeCount;

    private  boolean anonymous;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;


    public void TimeNow() {
        this.createdAt=LocalDateTime.now();
    }

    public void commentBasicSetting(String id,String username, String userId) {
        this.feedUID=id;
        this.username=username;
        this.userId=userId;
        this.createdAt=LocalDateTime.now();
        this.commentUID=UUID.randomUUID().toString();
    }

    public void commentAnonymousBasicSetting(String id) {
        this.feedUID=id;
        this.userId="";
        this.username="익명";
        this.createdAt=LocalDateTime.now();
        this.commentUID=UUID.randomUUID().toString();
    }


}
