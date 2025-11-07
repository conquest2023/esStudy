package es.board.controller.model.mapper;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.repository.document.Comment;

import java.time.LocalDateTime;

public class CommentDomainMapper {

    public static es.board.service.domain.Comment toDomain(CommentDTO.Response dto, Long postId) {
        if (dto == null) return null;

        Long id = dto.getId(); // commentUID가 UUID라면 null일 수 있음
        String userId =dto.getUserId();

        Long resolvedPostId =postId;

        LocalDateTime createdAt = dto.getCreatedAt();
        if (createdAt == null) createdAt = LocalDateTime.now();

        return new es.board.service.domain.Comment(
                id,
                resolvedPostId,
                userId,
                dto.getUsername(),
                dto.getContent(),
                dto.isAnonymous(),
                dto.getLikeCount(),
                createdAt,
                createdAt,
                null
        );
    }

    // Request DTO -> Domain (작성 시 사용)

    public static es.board.service.domain.Comment toDomain(CommentDTO.Request dto, Long postId) {
        if (dto == null) return null;

        Long id = dto.getId();
        Long resolvedPostId =postId;

        return new es.board.service.domain.Comment(
                id,
                resolvedPostId,
                dto.getUserId(),
                dto.getUsername(),
                dto.getContent(),
                dto.isAnonymous(),
                dto.getLikeCount(),
                dto.getCreatedAt() == null ? LocalDateTime.now() : dto.getCreatedAt(),
                dto.getCreatedAt() == null ? LocalDateTime.now() : dto.getCreatedAt(),
                null
        );
    }

    // Domain -> Response DTO (예시)
//    public static CommentDTO.Response toResponse(Comment d) {
//        if (d == null) return null;
//        CommentDto.Response resp = new CommentDto.Response();
//        resp.setCommentUID(d.getId() == null ? null : d.getId().toString());
//        resp.setUserId(d.getUserId() == null ? null : d.getUserId().toString());
//        resp.setFeedUID(d.getPostId() == null ? null : d.getPostId().toString());
//        resp.setUsername(d.getUsername());
//        resp.setContent(d.getContent());
//        resp.setLikeCount(d.getLikeCount());
//        resp.setAnonymous(d.isAnonymous());
//        resp.setCreatedAt(d.getCreatedAt());
//        return resp;
//    }
}
