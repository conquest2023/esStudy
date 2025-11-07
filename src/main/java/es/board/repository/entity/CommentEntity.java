package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment", schema = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "post_id", nullable = false)
    private Long postId;

    /**
     * 작성자 User PK
     * - User 엔티티와의 연관 매핑은 상황에 따라 나중에 추가
     */
    @Column(name = "user_id", nullable = false)
    private String userId;


    @Column(name = "username", nullable = false, length = 50)
    private String username;



    @Column(nullable = false)
    private String content;



    private boolean anonymous;


    @Column(name = "like_count", nullable = false)
    private int likeCount;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}