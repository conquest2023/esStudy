package es.board.controller.model.res;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.repository.document.Board;
import es.board.repository.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedCreateResponse {


    private  int id;

    private  String userId;


    private String feedUID;

    private String username;

    private  String imageURL;

    private int  viewCount;

    private String title;

    private  String category;

    private String description;

    private  boolean anonymous;

    private  int likeCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @JsonIgnore
    private MultipartFile attachFile;
    @JsonIgnore
    private List<MultipartFile> imageFiles;


    public void TimePush(){
        this.createdAt=LocalDateTime.now();
    }
}
