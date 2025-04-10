package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "question_written")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionWritten {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    private String category;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private Integer difficulty;

    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChoiceWritten> choices;
}