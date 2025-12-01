package es.board.controller.model.mapper;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.domain.Post;
import es.board.infrastructure.entity.feed.PostEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PostDomainMapper {

    public static Post toDomain(PostDTO.Response dto) {
        if (dto == null) return null;

        int id = dto.getId();
        String userId = dto.getUserId();
        LocalDateTime createdAt = dto.getCreatedAt();
        if (createdAt == null)
            createdAt = LocalDateTime.now();

        return new Post(
                id,
                userId,
                dto.getUsername(),
                dto.getTitle(),
                dto.getDescription(),
                dto.getCategory(),
                dto.getViewCount(),
                dto.isOwner(),
                createdAt,
                createdAt
        );
    }
    public static Post toDomain(String userId, PostDTO.Request dto) {
            if (dto == null) return null;

            LocalDateTime createdAt = dto.getCreatedAt();
            if (createdAt == null)
                createdAt = LocalDateTime.now();

            return new Post(
                    userId,
                    dto.getUsername(),
                    dto.getTitle(),
                    dto.getDescription(),
                    dto.getCategory(),
                    0,
                    createdAt
            );
        }
    public static Post toUpdateDomain(int id,String userId, PostDTO.Update dto) {
        LocalDateTime updateAt = LocalDateTime.now();
        return new Post(
                userId,
                dto.getTitle(),
                dto.getDescription(),
                updateAt);
    }


    public static PostDTO.Response toResponse(String userId, Post d) {
        if (d == null) return null;
        return PostDTO.Response.builder()
                .id(d.getId())
                .userId(d.getUserId())
                .owner(d.isOwnedBy(userId))
                .username(d.getUsername())
                .title(d.getTitle())
                .description(d.getDescription())
                .category(d.getCategory())
                .viewCount(d.getViewCount())
                .createdAt(d.getCreatedAt())
                .modifiedAt(d.getModifiedAt())
                .build();
    }
    public static List<PostDTO.Response> toResponse(Page<PostEntity> p) {
        return p.getContent().stream()
                .map(PostDomainMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static PostDTO.Response toResponse(PostEntity entity) {
        return new PostDTO.Response(
                entity.getId(),
                entity.getUsername(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getViewCount(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
//    public static List<PostDTO.Response> toRequestList(List<Post> posts) {
//        if (posts == null || posts.isEmpty()) return List.of();
//        return posts.stream()
//                .map(PostDomainMapper::toRequest)
//                .toList();
//    }
}
