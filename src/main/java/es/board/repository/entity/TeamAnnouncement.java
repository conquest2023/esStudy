package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_announcements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamAnnouncement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long teamId; // 팀 ID (FK)

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isPinned; // 공지 고정 여부

    @Column(nullable = false)
    private Long createdBy; // 공지를 작성한 유저 ID
}
