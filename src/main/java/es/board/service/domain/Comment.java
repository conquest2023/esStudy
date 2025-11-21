package es.board.service.domain;

import es.board.repository.entity.feed.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private Long id;
    private int postId;
    private String  userId;
    private String username;
    private String content;
    private boolean isAuthor;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Comment(Long id, int postId, String userId, String username, String content, boolean anonymous, int likeCount, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.isAuthor = anonymous;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }

    public static CommentEntity toEntity(Comment d) {
        if (d == null) return null;
        return CommentEntity.builder()
                .id(d.getId())
                .postId(d.getPostId())
                .userId(d.getUserId())
                .username(d.getUsername())
                .content(d.getContent())
                .anonymous(d.isAuthor())
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
    public  boolean isOwnedBy(String currentUserId) {
        return currentUserId != null && Objects.equals(userId, currentUserId);
    }
    public boolean isAuthorBy(String postOwnerId){
        return  postOwnerId !=null &&Objects.equals(postOwnerId,userId);
    }

    public static List<Comment> toDomainList(List<CommentEntity> entities) {
        if (entities == null || entities.isEmpty())
            return List.of();
        return entities.stream()
                .map(Comment::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return postId == comment.postId && isAuthor == comment.isAuthor && likeCount == comment.likeCount && Objects.equals(id, comment.id) && Objects.equals(userId, comment.userId) && Objects.equals(username, comment.username) && Objects.equals(content, comment.content) && Objects.equals(createdAt, comment.createdAt) && Objects.equals(updatedAt, comment.updatedAt) && Objects.equals(deletedAt, comment.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postId, userId, username, content, isAuthor, likeCount, createdAt, updatedAt, deletedAt);
    }
}
