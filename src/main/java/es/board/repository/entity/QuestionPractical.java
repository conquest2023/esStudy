package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_practical")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionPractical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    private String category;

    private String type;
    @Column(columnDefinition = "TEXT")
    private String questionText;
    @Column(columnDefinition = "TEXT")
    private String modelAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private Integer difficulty;

    private LocalDateTime createdAt;


}