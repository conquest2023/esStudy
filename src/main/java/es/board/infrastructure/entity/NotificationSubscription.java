package es.board.infrastructure.entity;

import es.board.infrastructure.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_subscriptions")
public class NotificationSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String endpoint;

    @Column(nullable = false)
    private String p256dh;

    @Column(nullable = false)
    private String auth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId") // DB의 userId 컬럼을 바라보게 설정
    private User user;

    @Builder
    public NotificationSubscription(String endpoint, String p256dh, String auth, User user) {
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
        this.user = user;
    }
}