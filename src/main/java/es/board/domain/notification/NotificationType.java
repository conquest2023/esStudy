package es.board.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    // 피드 관련
    COMMENT("notifications:comment:", "comment-notification"),
    REPLY("notifications:reply:", "reply-notification"),
    LIKE("notifications:like:", "like-notification"),

    // 시스템 및 사용자 활동 관련
    TODO("notifications:todo:", "todo-notification"),
    NOTICE("notifications:notice:", "notice-notification"),
    POINT("notifications:point:", "point-notification"),
    ANALYSIS("notifications:analysis:", "analysis-notification"),
    POLL("notifications:poll:", "poll-notification"),

    // 랭킹 관련
    RANK_TOP3("notifications:rank:", "rank-top3-notification"),
    RANK_TOP1("notifications:rank1:", "rank-top1-notification"),

    //영어
    ENGLISH_PRACTICE("notifications:english:","english-practice-notification");

    private final String redisKeyPrefix;
    private final String eventName;

    /**
     * 특정 사용자 전용 Redis Key 생성
     */
    public String getRedisKey(String userId) {
        return this.redisKeyPrefix + userId;
    }
}