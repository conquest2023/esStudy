package es.board.infrastructure.entity.todo;

import es.board.repository.entity.TodoStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "todo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long todoId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TodoStatus status;
    @Column(name = "priority")
    private Integer priority;

    @Column(name = "project")
    private Boolean project;

    @OneToOne(mappedBy = "todo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private TodoRecurrenceEntity recurrence;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Column(name = "end")
    private LocalDateTime end;

    public void complete() {
        if (this.status == TodoStatus.DONE) {
            throw new IllegalStateException("이미 완료된 할 일입니다.");
        }
        this.status = TodoStatus.DONE;
        this.end=LocalDateTime.now();
    }

}
