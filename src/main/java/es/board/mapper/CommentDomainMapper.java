package es.board.mapper;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.repository.entity.Notification;
import es.board.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDomainMapper {

    public static Comment toDomain(String userId, CommentDTO.Response dto, int postId) {
        if (dto == null) return null;

        Long id = dto.getId(); // commentUID가 UUID라면 null일 수 있음

        int resolvedPostId = postId;

        LocalDateTime createdAt = dto.getCreatedAt();
        if (createdAt == null) createdAt = LocalDateTime.now();

        return new Comment(
                id,
                resolvedPostId,
                userId,
                dto.getUsername(),
                dto.getContent(),
                dto.isAnonymous(),
                dto.getLikeCount(),
                createdAt
        );
    }
    public static Notification toEntityNotification(String userId, CommentDTO.Response comment) {
        return Notification.builder()
                .userId(userId)
                .postId(comment.getPostId())
                .isCheck(false)
                .username(comment.getUsername())
                .sender(comment.getUserId())
                .message(comment.getContent())
                .type("댓글")
                .username(comment.getUsername())
                .createdAt(LocalDateTime.now())
                .build();
    }
    // Request DTO -> Domain (작성 시 사용)

    public static CommentDTO.Request toRequestDto(String userId,String postOwnerId, Comment domain) {
        if (domain == null) return null;
        return CommentDTO.Request.builder()
                .id(domain.getId())
                .owner(domain.isOwnedBy(userId))
                .username(domain.getUsername())
                .content(domain.getContent())
                .isAuthor(domain.isAuthorBy(postOwnerId))
                .likeCount(domain.getLikeCount())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public static CommentDTO.Request toUpdateDto(String userId, Comment domain) {
        if (domain == null) return null;
        return CommentDTO.Request.builder()
                .id(domain.getId())
                .owner(domain.isOwnedBy(userId))
                .username(domain.getUsername())
                .content(domain.getContent())
                .likeCount(domain.getLikeCount())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    public static List<CommentDTO.Request> toRequestDtoList(String userId,String postOwnerId,List<Comment> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(d -> toRequestDto(userId,postOwnerId, d))
                .collect(Collectors.toList());
    }
}
