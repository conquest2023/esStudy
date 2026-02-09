package es.board.infrastructure.english.collcetion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@Document(collection = "problems")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnglishProblem {

    @Id
    private String id;

    private String category;
    private String type;

    private int part;

    private String level;

    private ProblemContent content;

    private List<String> tags;
    @CreatedDate
    private LocalDateTime createdAt;
}
