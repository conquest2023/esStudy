package es.board.controller.model.res;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.repository.document.Board;
import es.board.repository.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedCreateResponse {


    private  String id;

    private String feedUID;

    private String username;

    private  String userId;

    private  String image;

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

//    private MultipartFile attachFile;
//
//    private List<MultipartFile> imageFiles;
//
//    private String attachFileBase64;
//
//    private List<String> base64ImageFiles;









    public void TimePush(){
        this.createdAt=LocalDateTime.now();
    }
}
