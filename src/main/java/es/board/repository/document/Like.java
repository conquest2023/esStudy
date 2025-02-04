package es.board.repository.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.time.LocalDateTime;


@Document(indexName = "Like")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Like {


    private String userId;

    private String feedUID;


    private LocalDateTime createdAt;

}
