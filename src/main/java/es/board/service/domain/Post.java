package es.board.service.domain;

import es.board.repository.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    private int id;

//    private Long feedId;

    private String userId;

    private String username;

    private String title;

    private String description;

    private String category;

    private int viewCount;

    private int likeCount;

    private boolean anonymous;

    private String imageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public static PostEntity toEntity(Post d) {
        if (d == null) return null;
        return PostEntity.builder()
                .id(d.getId())
//                .feedId(d.getFeedId())
                .userId(d.getUserId())
                .username(d.getUsername())
                .category(d.getCategory())
                .description(d.getDescription())
                .title(d.getTitle())
                .anonymous(d.isAnonymous())
                .imageUrl(d.getImageUrl())
                .createdAt(d.getCreatedAt())
                .modifiedAt(d.getModifiedAt())
                .build();
    }

    // Entity -> Domain
    public static Post toDomain(PostEntity e) {
        if (e == null) return null;
        return new Post(
                e.getId(),
                e.getUserId(),
                e.getUsername(),
                e.getTitle(),
                e.getDescription(),
                e.getCategory(),
                e.getViewCount(),
                e.getLikeCount(),
                e.isAnonymous(),
                e.getImageUrl(),
                e.getCreatedAt(),
                e.getModifiedAt()
        );
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", anonymous=" + anonymous +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}
