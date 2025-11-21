package es.board.controller.model.mapper;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.service.domain.Post;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class PostDomainMapper {

    public static Post toDomain(PostDTO.Request dto) {
        if (dto == null) return null;

        int id = dto.getId();
        String userId = dto.getUserId();    // @JsonIgnore지만 내부적으로 채워질 수 있으니 대응
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
                dto.getLikeCount(),
                dto.getViewCount(),
                dto.isOwner(),
                dto.getImageURL(),
                createdAt,
                createdAt                     // modifiedAt 초기값
        );
    }
    public static Post toDomain(String userId, PostDTO.Response dto) {
            if (dto == null) return null;

            int  id      = dto.getId();
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
                    0,
                    0,
                    dto.isAnonymous(),
                    dto.getImageURL(),
                    createdAt
            );
        }
    public static Post toUpdateDomain(int id,String userId, PostDTO.Update dto) {
        LocalDateTime updateAt = LocalDateTime.now();
        return new Post(
                id,
                userId,
                dto.getTitle(),
                dto.getDescription(),
                updateAt);
    }


    /** Domain → Request DTO (주로 수정/재전송 용도로 필요 시) */
    public static PostDTO.Request toRequest(String userId, Post d) {
        if (d == null) return null;
        return PostDTO.Request.builder()
                .id(d.getId())
                .userId(d.getUserId())
                .owner(d.isOwnedBy(userId))
                .username(d.getUsername())
                .imageURL(d.getImageUrl())
                .title(d.getTitle())
                .description(d.getDescription())
                .category(d.getCategory())
                .viewCount(d.getViewCount())
                .createdAt(d.getCreatedAt())
                .modifiedAt(d.getModifiedAt())
                .build();
    }
//    public static List<PostDTO.Request> toRequestList(List<Post> posts) {
//        if (posts == null || posts.isEmpty()) return List.of();
//        return posts.stream()
//                .map(PostDomainMapper::toRequest)
//                .toList();
//    }
}
