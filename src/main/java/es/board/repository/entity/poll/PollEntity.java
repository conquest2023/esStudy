package es.board.repository.entity.poll;

import es.board.repository.entity.feed.PostEntity;
import es.board.service.domain.Post;
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
    private boolean multiSelect;  // 0/1 → boolean 매핑

    @Column(name = "max_select_cnt")
    private Integer maxSelectCnt; // 복수 선택일 때 최대 개수

    @Column(name = "is_anonymous", nullable = false)
    private boolean anonymous; // 익명 투표 여부

    @Column(name = "closes_at")
    private LocalDateTime closesAt; // 마감 시간

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PollOptionEntity> options = new ArrayList<>();

    // 투표 내역 (필요 없으면 삭제해도 됨)
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