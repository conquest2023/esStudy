package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_points")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPoints {

    @Id
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "total_points", nullable = false)
    private int totalPoints;

    @Column(name = "redeemed_points", nullable = false)
    private int redeemedPoints;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}

