package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

//    @Transient
//    private  String  username;

    @Column(name = "point_change", nullable = false)
    private int pointChange;

    @Column(nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PointHistoryEntity(String username, Long pointSum) {
//        this.username=username;
        this.pointChange = pointSum.intValue();
    }
}
