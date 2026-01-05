package es.board.infrastructure.entity.todo;


import es.board.controller.model.dto.todo.DayOfWeekType;

import java.io.Serializable;
import java.util.Objects;

public class TodoRecurrenceWeekdayId implements Serializable {
    private Long recurrenceId;
    private DayOfWeekType weekday;

    public TodoRecurrenceWeekdayId() {}

    public TodoRecurrenceWeekdayId(Long recurrenceId, DayOfWeekType weekday) {
        this.recurrenceId = recurrenceId;
        this.weekday = weekday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoRecurrenceWeekdayId that)) return false;
        return Objects.equals(recurrenceId, that.recurrenceId) && weekday == that.weekday;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recurrenceId, weekday);
    }
}
