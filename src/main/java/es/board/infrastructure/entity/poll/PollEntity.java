package es.board.infrastructure.entity.poll;

import es.board.infrastructure.entity.feed.PostEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "poll")
public class PollEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private PostEntity post;


    @Column(name = "user_id")
    private String userId;

    @Column(name = "multi_select", nullable = false)
    private boolean multiSelect;

    @Column(name = "max_select_cnt")
    private Integer maxSelectCnt;

    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous;

    @Column(name = "closes_at")
    private LocalDateTime closesAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PollOptionEntity> options = new ArrayList<>();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PollVoteEntity> votes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // 연관관계 편의 메서드 (옵션 추가)
    public void addPoll(PollOptionEntity option){
         options.add(option);
         option.setPoll(this);
    }
}