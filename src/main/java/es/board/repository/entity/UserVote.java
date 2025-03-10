package es.board.repository.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_votes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "upvote", nullable = false)
    private boolean upvote;
}