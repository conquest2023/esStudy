package es.board.repository.document;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.model.res.FeedCreateResponse;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(indexName = "board")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Board {

    @Id
    @Field(name="id", type = FieldType.Keyword)
    private String feedUID;

    @Field
    private String username;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;


    @Field(type = FieldType.Integer)
    private Integer likeCount;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;


    @Field(type=FieldType.Date)
    private LocalDate updatedAt;

    @Field(type=FieldType.Date)
    private LocalDate deletedAt;




    public Board BoardToEntity(FeedCreateResponse feedSaveDTO) {
        return Board.builder()
                .feedUID(feedSaveDTO.getFeedUID())
                .username(feedSaveDTO.getUsername())
                .title(feedSaveDTO.getTitle())
                .description(feedSaveDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }


}
