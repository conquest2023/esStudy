package es.board.repository.document;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "board")
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
@Builder
public class Board {

    @Id
    private  int id;
    @Field
    private String feedUID;

    private String userId;

    @Field
    private String username;


    private  String imageURL;


    @Field(type =FieldType.Keyword)
    private String  attachFile;

    @Field(type =FieldType.Keyword)
    private List<String> imageFiles;


    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;


    @Field(type = FieldType.Text)
    private String category;


    @Field(type = FieldType.Integer)
    private int likeCount;

    @Field(type = FieldType.Integer)
    private  int viewCount;

    private  boolean anonymous;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDate updatedAt;

    @Field(type=FieldType.Date)
    private LocalDate deletedAt;






    public  void plusCount(){
        viewCount++;
    }
}
