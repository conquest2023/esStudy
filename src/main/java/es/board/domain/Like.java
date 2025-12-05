package es.board.domain;

import es.board.domain.enum_type.TargetType;

import java.time.LocalDateTime;

public class Like {

    private long id;

    private final int postId;
    private final String userId;
    private final long targetId;

    private final TargetType targetType;

    private final LocalDateTime createdAt;


    public boolean isOwner(String ownerId){
         return  this.userId.equals(ownerId);
    }

    public Like(int postId, String userId, long targetId, TargetType targetType, LocalDateTime createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.createdAt = createdAt;
    }

    public static Like of(long id, int postId, String userId, long targetId, TargetType targetType, LocalDateTime createdAt) {
        return new Like(id,postId, userId, targetId, targetType, createdAt);
    }

    public static Like of(int postId, String userId, long targetId, TargetType targetType, LocalDateTime createdAt) {
        return new Like(postId, userId, targetId, targetType, createdAt);
    }

    public Like(long id, int postId, String userId, long targetId, TargetType targetType, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public int getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public long getTargetId() {
        return targetId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


}
