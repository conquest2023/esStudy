package es.board.repository.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;


@Document(indexName = "Like")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Like {

    @Id
    private String likeID;

    private String feedUID;


}
