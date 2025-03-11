package es.board.repository.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "vote_id", nullable = false)
    private Long voteId;

    private  String  username;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "upvote", nullable = false)
    private boolean upvote;


    @Column(name = "created_at")
    private LocalDateTime createdAt;
}