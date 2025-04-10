package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "choice_written")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceWritten {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long choiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private QuestionWritten question;

    @Column(columnDefinition = "TEXT")
    private String choiceText;

    private Boolean isCorrect;
}
