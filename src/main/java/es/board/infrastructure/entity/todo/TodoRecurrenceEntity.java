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


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "todo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_recurrence_todo"))
    @Setter
    private TodoEntity todo;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", nullable = false, length = 20)
    private RepeatType repeatType;


    @Column(name = "interval_value", nullable = false)
    private Integer intervalValue;

    @Column(name = "repeat_end_date")
    private LocalDate repeatEndDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


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
