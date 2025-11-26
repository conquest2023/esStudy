package es.board.infrastructure.entity.feed;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_images")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id")
    private int postId;

    private int seq;

    @Column(name = "display_width")
    private int displayWidth;

    @Column(name = "display_height")
    private int displayHeight;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt ;
}
