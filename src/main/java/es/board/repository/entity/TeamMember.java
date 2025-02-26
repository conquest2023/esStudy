package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long teamId; // 팀 ID (FK)

    @Column(nullable = false)
    private Long userId; // 유저 ID (FK)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 'ADMIN', 'MEMBER'

    public enum Role {
        ADMIN, MEMBER
    }
}
