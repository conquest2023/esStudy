package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "tistory_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TistoryPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키 (자동 증가)

    private  String name;

    private String blogName;       // 블로그 이름
    private String blogUrl;        // 블로그 URL
    private String title;          // 게시글 제목
    private String postUrl;        // 게시글 URL

    @Column(columnDefinition = "TEXT")
    private String description;    // 게시글 설명 (TEXT 타입)
    private String thumbnailUrl;   // 썸네일 이미지 URL

//    private l createdAt; // 데이터 생성 시간
}
