package es.board.controller.model.mapper.entity;

import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.repository.entity.Notification;
import es.board.domain.Reply;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReplyDomainMapper {
    public static Reply toDomain(String userId, ReplyDTO.Response dto) {
        if (dto == null) return null;

        Long id        =dto.getReplyId();
        LocalDateTime createdAt = dto.getCreatedAt();
        if (createdAt == null) createdAt = LocalDateTime.now();

        return new Reply(
                id,
                dto.getCommentId(),
                dto.getPostId(),
                userId,
                dto.getUsername(),
                dto.getContent(),
                dto.getLikeCount(),
//                createdAt,
                null,
                null
        );
    }

    // Response DTO -> Domain

    public static ReplyDTO.Request toRequestDto(String userId,String postOwnerId, Reply d) {
        if (d == null) return null;
        return new ReplyDTO.Request(
                d.getId(),
                d.getCommentId(),
                d.getPostId(),
                d.isOwnedBy(userId),
                d.isAuthorBy(postOwnerId),
                d.getUsername(),
                d.getContent(),
                d.getLikeCount(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }

    public static ReplyDTO.Request toUpdateDto(String userId, Reply d) {
        if (d == null) return null;
        return new ReplyDTO.Request(
                d.getId(),
                d.getCommentId(),
                d.getPostId(),
                d.isOwnedBy(userId),
                d.getUsername(),
                d.getContent(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }
    public static List<ReplyDTO.Request> toRequestDtoList(String userId,String postOwnerId, List<Reply> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .filter(Objects::nonNull)
                .map(d -> toRequestDto(userId,postOwnerId, d))
                .collect(Collectors.toList());
    }

    public static Notification toEntityNotification(String userId, ReplyDTO.Response reply) {
        return Notification.builder()
                .userId(userId)
                .postId(reply.getPostId())
                .isCheck(false)
                .username(reply.getUsername())
                .sender(reply.getUserId())
                .message(reply.getContent())
                .type("답글")
                .username(reply.getUsername())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
