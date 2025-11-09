package es.board.service.domain;

import es.board.controller.model.mapper.ReplyDomainMapper;
import es.board.repository.entity.ReplyEntity;
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
public class Reply {

    private Long id;
    private Long commentId;
    private int postId;
    private String userId;
    private String username;
    private String content;
//    private boolean anonymous;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


    public  boolean isOwnedBy(String currentUserId) {
        return currentUserId != null && Objects.equals(userId, currentUserId);
    }
    public static ReplyEntity toEntity(Reply d) {
        if (d == null) return null;
        return ReplyEntity.builder()
                .id(d.getId())
                .commentId(d.getCommentId())
                .postId(d.getPostId())
                .userId(d.getUserId())
                .username(d.getUsername())
                .content(d.getContent())
                .likeCount(d.getLikeCount())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .deletedAt(d.getDeletedAt())
                .build();
    }

    public static List<Reply> toDomainList(List<ReplyEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .filter(Objects::nonNull)
                .map(Reply::toDomain)
                .collect(Collectors.toList());
    }



    public static Reply toDomain(ReplyEntity e) {
        if (e == null) return null;
        return new Reply(
                e.getId(),
                e.getCommentId(),
                e.getPostId(),
                e.getUserId(),
                e.getUsername(),
                e.getContent(),
                e.getLikeCount(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt()
        );
    }
}
