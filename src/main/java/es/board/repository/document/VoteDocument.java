package es.board.repository.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "data_votes")
public class VoteDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long, name = "post_id")
    private Long postId;

    @Field(type = FieldType.Keyword, name = "feedUID")
    private String feedUID;

    @Field(type = FieldType.Keyword, name = "userId")
    private String userId;

    @Field(type = FieldType.Text, name = "username")
    private String username;

    @Field(type = FieldType.Text, fielddata = true)
    private String description;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String selectedOption;

    @Field(type = FieldType.Keyword)
    private List<String> voteType;

    @Field(type = FieldType.Boolean, name = "upvote")
    private boolean upvote;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "description")

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
}
