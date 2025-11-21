package es.board.service.domain;

import es.board.repository.entity.feed.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    private int id;

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

    public Post(int id, String userId, String title, String description, LocalDateTime updateAt) {
        this.id=id;
        this.userId=userId;
        this.title=title;
        this.description=description;
        this.modifiedAt=updateAt;
    }

    public Post(int id, String userId, String username, String title, String description, String category, int viewCount, int likeCount, boolean anonymous, String imageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.description = description;
        this.category = category;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.anonymous = anonymous;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

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

    public PostEntity update(Post post){
        return PostEntity.builder()
                .userId(post.getUserId())
                .title(post.getTitle())
                .description(post.getDescription())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
    public  boolean isOwnedBy(String currentUserId) {
        return currentUserId != null && Objects.equals(userId, currentUserId);
    }
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
