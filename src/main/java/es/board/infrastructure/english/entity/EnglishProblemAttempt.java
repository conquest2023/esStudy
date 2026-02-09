package es.board.infrastructure.english.entity;

import es.board.controller.model.dto.english.EnglishProblemAttemptDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "english_problem_attempt",
        indexes = {
                @Index(name = "idx_user_created", columnList = "user_id, created_at"),
                @Index(name = "idx_user_object", columnList = "user_id, object_id"),
                @Index(name = "idx_object_created", columnList = "object_id, created_at")
        }
)
public class EnglishProblemAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "object_id", length = 24, nullable = false)
    private String objectId;

    @Column(name = "chosen_answer", length = 10, nullable = false)
    private String chosenAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @Column(name = "part")
    private Integer part;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 10)
    private Level level;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;


    public enum Level {
        BRONZE, SILVER, GOLD
    }


    public EnglishProblemAttempt(String userId, String objectId, String chosenAnswer, Boolean isCorrect, String category, Integer part, Level level, LocalDateTime createdAt) {
        this.userId = userId;
        this.objectId = objectId;
        this.chosenAnswer = chosenAnswer;
        this.isCorrect = isCorrect;
        this.category = category;
        this.part = part;
        this.level = level;
        this.createdAt = createdAt;
    }

    public  static EnglishProblemAttempt toEntity(String userId, EnglishProblemAttemptDto.Request request) {
        return  new EnglishProblemAttempt(
                userId,
                request.getObjectId(),
                request.getChosenAnswer(),
                request.getIsCorrect(),
                request.getCategory(),
                request.getPart(),
                request.getLevel(),
                LocalDateTime.now());
    }
}

