package es.board.repository.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
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
    private LocalDateTime startDatetime;

    private LocalDateTime endDatetime;

    @Column(nullable = false)
    private Boolean allDay; // 하루 종일 여부

    private String location;

    private  String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정일

    @Column(name = "is_repeat")
    private Boolean isRepeat;

    @Column(name = "repeat_days")
    private String repeatDays; // 🔄 반복 요일 (예: "월,수,금")

    @Column(nullable = true,name = "repeat_start_date")
    private LocalDateTime repeatStartDate; // 🔄 반복 일정 시작 날짜

    @Column(nullable = true,name = "repeat_end_date")
    private LocalDateTime repeatEndDate; // 🔄 반복 일정 종료 날짜

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
