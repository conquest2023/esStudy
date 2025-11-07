package es.board.controller.model.mapper;

import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.service.domain.Reply;

import java.time.LocalDateTime;

public class ReplyDomainMapper {
    public static Reply toDomain(ReplyDTO.Response dto, Long postId, Long commentId) {
        if (dto == null) return null;

        Long id        =dto.getReplyId();
        String userId    =dto.getUserId();
        Long resolvedCommentId = commentId ;

        Long resolvedPostId    = postId;

        LocalDateTime createdAt = dto.getCreatedAt();
        if (createdAt == null) createdAt = LocalDateTime.now();

        return new Reply(
                id,
                resolvedCommentId,
                resolvedPostId,
                userId,
                dto.getUsername(),
                dto.getContent(),
                false,                    // anonymous 정보가 DTO에 없으면 기본 false
                dto.getLikeCount(),
                createdAt,
                createdAt,
                null
        );
    }

    // Request DTO -> Domain

    public static Reply toDomain(ReplyDTO.Request dto, Long postId, Long commentId) {
        if (dto == null) return null;

        Long id        = dto.getReplyId();
        Long resolvedCommentId = commentId;
        Long resolvedPostId    = postId;
        return new Reply(
                id,
                resolvedCommentId,
                resolvedPostId,
                dto.getUserId(),
                dto.getUsername(),
                dto.getContent(),
                false,
                dto.getLikeCount(),
                dto.getCreatedAt() == null ? LocalDateTime.now() : dto.getCreatedAt(),
                dto.getCreatedAt() == null ? LocalDateTime.now() : dto.getCreatedAt(),
                null
        );
    }
}
