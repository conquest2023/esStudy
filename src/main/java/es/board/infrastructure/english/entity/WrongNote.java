package es.board.infrastructure.english.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "wrong_note",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_object", columnNames = {"user_id", "object_id"})
        },
        indexes = {
                @Index(name = "idx_user_lastwrong", columnList = "user_id, last_wrong_at"),
                @Index(name = "idx_user_filter", columnList = "user_id, category, part, level, resolved")
        }
)
public class WrongNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "object_id", length = 24, nullable = false)
    private String objectId;

    @Column(name = "category", length = 50, nullable = false)
    private String category;

    @Column(name = "part")
    private Integer part;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 10)
    private EnglishProblemAttempt.Level level; // 같은 enum 재사용 (원하면 분리해도 됨)

    @Column(name = "wrong_count", nullable = false)
    private Integer wrongCount;

    @Column(name = "last_wrong_at", nullable = false, insertable = false, updatable = true)
    private LocalDateTime lastWrongAt;

    @Column(name = "first_wrong_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime firstWrongAt;

    @Column(name = "resolved", nullable = false)
    private Boolean resolved;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public void markResolved() {
        this.resolved = true;
        this.resolvedAt = LocalDateTime.now();
    }

    public void unresolve() {
        this.resolved = false;
        this.resolvedAt = null;
    }

    public void increaseWrongCount() {
        this.wrongCount = (this.wrongCount == null ? 1 : this.wrongCount + 1);
        this.lastWrongAt = LocalDateTime.now();
        this.resolved = false;
        this.resolvedAt = null;
    }
}