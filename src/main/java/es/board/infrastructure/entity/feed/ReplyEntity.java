package es.board.infrastructure.entity.feed;

//p

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reply", schema = "board")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


    public void applyFrom(String content){
        this.content=content;
        this.updatedAt=LocalDateTime.now();
    }

}
