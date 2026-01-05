package es.board.infrastructure.entity.todo;

import es.board.controller.model.dto.todo.DayOfWeekType;
import es.board.controller.model.dto.todo.RepeatType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "todo_recurrence")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TodoRecurrenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurrence_id")
    private Long recurrenceId;

    // ✅ Todo 1개당 recurrence 0..1 (todo_id UNIQUE는 DB에서 잡는 걸 권장)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "todo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_recurrence_todo"))
    @Setter // attachRecurrence에서 역방향 세팅 필요
    private TodoEntity todo;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", nullable = false, length = 20)
    private RepeatType repeatType;

    // 1이면 매주/매달, 2면 격주/격달
    @Column(name = "interval_value", nullable = false)
    private Integer intervalValue;

    @Column(name = "repeat_end_date")
    private LocalDate repeatEndDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ✅ WEEKLY일 때만 값 존재 (MON/WED/FRI...)
    @OneToMany(mappedBy = "recurrence", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TodoRecurrenceWeekdayEntity> weekdays = new HashSet<>();

    public void addWeekday(DayOfWeekType weekday) {
        TodoRecurrenceWeekdayEntity e = TodoRecurrenceWeekdayEntity.builder()
                .recurrence(this)
                .weekday(weekday)
                .build();
        this.weekdays.add(e);
    }

    public void clearWeekdays() {
        this.weekdays.clear();
    }
}
