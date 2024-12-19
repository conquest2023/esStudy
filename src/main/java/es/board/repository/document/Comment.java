package es.board.repository.document;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.model.req.CommentUpdate;
import es.board.model.res.CommentCreateResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.*;

@Document(indexName = "comment")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Comment{

    @Id
    @Field(name="id", type = FieldType.Keyword)
    private String commentUID;


    @Field(type=FieldType.Keyword)
    private String feedUID;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Integer)
    private int likeCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;

//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
//    private LocalDateTime deletedAt;



    public Comment CommentToEntity(CommentCreateResponse commentSaveDTO) {
        return Comment.builder()
                .commentUID(commentSaveDTO.getCommentUID())
                .username(commentSaveDTO.getUsername())
                .content(commentSaveDTO.getContent())
                .likeCount(commentSaveDTO.getLikeCount())
                .createdAt(LocalDateTime.now()).build();
    }



    public Comment convertDtoToEntity(CommentUpdate eq) {
            return  Comment.builder()
                    .commentUID(eq.getCommentUID())
                    .username(eq.getUsername())
                    .content(eq.getContent())
                    .build();
    }



}
