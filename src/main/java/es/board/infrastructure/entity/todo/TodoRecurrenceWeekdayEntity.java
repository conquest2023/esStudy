package es.board.infrastructure.entity.todo;

import es.board.controller.model.dto.todo.DayOfWeekType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todo_recurrence_weekday")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@IdClass(TodoRecurrenceWeekdayId.class)
public class TodoRecurrenceWeekdayEntity {

    @Id
    @Column(name = "recurrence_id", nullable = false)
    private Long recurrenceId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false, length = 3)
    private DayOfWeekType weekday;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recurrence_id", referencedColumnName = "recurrence_id",
            insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_weekday_recurrence"))
    private TodoRecurrenceEntity recurrence;

    // builder에서 recurrence만 넣어도 recurrenceId 세팅되도록
    public static TodoRecurrenceWeekdayEntity of(TodoRecurrenceEntity recurrence, DayOfWeekType weekday) {
        return TodoRecurrenceWeekdayEntity.builder()
                .recurrence(recurrence)
                .recurrenceId(recurrence.getRecurrenceId())
                .weekday(weekday)
                .build();
    }
}
