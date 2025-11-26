package es.board.infrastructure.entity.feed;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notice")
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "feed_id")
//    private  String feedUID;

    @Column(name = "user_id")
    private  String userId;


    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private PostEntity post;


//    private  String  category;
//
//    @Column(nullable = false, length = 255)
//    private String title;
//
//    private String description;
//
//
//    private String username;

//    @Column(name = "image_url")
//    private String  imageURL;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
    }

//    @PreUpdate
//    protected void onUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }
}