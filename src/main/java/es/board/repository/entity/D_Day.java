package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity

@Data
@Table(name = "d_day")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class D_Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 100)
    private String examName;

    @Column(nullable = false)
    private LocalDate examDate;

    @Column(length = 255)
    private String goal;

    private Integer progress;

    @Transient
    private  long dDay;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


}
