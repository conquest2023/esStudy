package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId; // 일정 ID (PK)

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
    private String  userId; // 사용자 (FK)

    @Column(nullable = false)
    private String title; // 일정 제목

    @Column(nullable = false)
    private LocalDateTime startDatetime; // 시작 시간

    private LocalDateTime endDatetime; // 종료 시간 (옵션)

    @Column(nullable = false)
    private Boolean allDay; // 하루 종일 여부

    private String location; // 장소 (옵션)

    @Column(columnDefinition = "TEXT")
    private String description; // 설명 (옵션)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정일

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
