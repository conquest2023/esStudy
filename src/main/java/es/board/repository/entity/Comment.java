package es.board.repository.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.model.req.UpdateCommentDTO;
import es.board.model.res.CommentSaveDTO;
import es.board.model.res.FeedSaveDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Document(indexName = "comment")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Comment{

    @Id
    @Field(name="id", type = FieldType.Keyword)
    private String commentUID;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Integer)
    private int likeCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime updatedAt;


    private LocalDateTime deletedAt;



    public Comment CommentToEntity(CommentSaveDTO commentSaveDTO) {
        return Comment.builder()
                .commentUID(commentSaveDTO.getCommentUID())
                .username(commentSaveDTO.getUsername())
                .content(commentSaveDTO.getContent())
                .likeCount(commentSaveDTO.getLikeCount())
                .createdAt(LocalDateTime.now()).build();
    }



    public Comment convertDtoToEntity(UpdateCommentDTO eq) {
            return  Comment.builder()
                    .commentUID(eq.getCommentUID())
                    .username(eq.getUsername())
                    .content(eq.getContent())
                    .build();
    }

}
