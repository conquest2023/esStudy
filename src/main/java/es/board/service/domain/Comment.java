package es.board.service.domain;

import es.board.repository.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private Long id;
    private Long postId;
    private String  userId;
    private String username;
    private String content;
    private boolean anonymous;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static CommentEntity toEntity(Comment d) {
        if (d == null) return null;
        return CommentEntity.builder()
                .id(d.getId())
                .postId(d.getPostId())
                .userId(d.getUserId())
                .username(d.getUsername())
                .content(d.getContent())
                .anonymous(d.isAnonymous())
                .likeCount(d.getLikeCount())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .deletedAt(d.getDeletedAt())
                .build();
    }

    public static Comment toDomain(CommentEntity e) {
        if (e == null) return null;
        return new Comment(
                e.getId(),
                e.getPostId(),
                e.getUserId(),
                e.getUsername(),
                e.getContent(),
                e.isAnonymous(),
                e.getLikeCount(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt()
        );
    }
}
