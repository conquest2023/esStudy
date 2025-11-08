package es.board.controller.model.mapper;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.service.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDomainMapper {

    public static es.board.service.domain.Comment toDomain(String userId, CommentDTO.Response dto, int postId) {
        if (dto == null) return null;

        Long id = dto.getId(); // commentUID가 UUID라면 null일 수 있음

        int resolvedPostId = postId;

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

    public static CommentDTO.Request toRequestDto(String userId, Comment domain) {
        if (domain == null) return null;
        return CommentDTO.Request.builder()
                .id(domain.getId())
                .owner(userId.equals(domain.getUserId()))
                .username(domain.getUsername())
                .content(domain.getContent())
                .anonymous(domain.isAnonymous())
                .likeCount(domain.getLikeCount())
                .createdAt(domain.getCreatedAt())
                .build();
    }
    public static List<CommentDTO.Request> toRequestDtoList(String userId,List<Comment> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(d -> toRequestDto(userId, d))
                .collect(Collectors.toList());
    }
}
