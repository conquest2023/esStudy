package es.board.repository.entity;

//p

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reply", schema = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "comment_id", nullable = false)
    private Long commentId;


    @Column(name = "post_id", nullable = false)
    private int postId;


    @Column(name = "user_id", nullable = false)
    private String userId;


    @Column(name = "username", nullable = false, length = 50)
    private String username;



    @Column(nullable = false)
    private String content;


    @Column(nullable = false)
    private boolean anonymous;


    @Column(name = "like_count", nullable = false)
    private int likeCount;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시각
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 소프트 삭제 시각
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 엔티티 저장 전 자동 시간 설정
     */
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
