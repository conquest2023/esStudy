package es.board.controller.model.mapper;

import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.service.domain.Reply;

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
                createdAt,
                createdAt,
                null
        );
    }

    // Request DTO -> Domain

    public static ReplyDTO.Request toRequestDto(String userId, Reply d) {
        if (d == null) return null;
        return new ReplyDTO.Request(
                d.getId(),
                d.getCommentId(),
                d.getPostId(),
                userId.equals(d.getUserId()),
                d.getUsername(),
                d.getContent(),
                d.getLikeCount(),
                d.getCreatedAt()
        );
    }
    public static List<ReplyDTO.Request> toRequestDtoList(String userId, List<Reply> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .filter(Objects::nonNull)
                .map(d -> toRequestDto(userId, d))
                .collect(Collectors.toList());
        // 또는 .toList() (JDK 16+)
    }
}
