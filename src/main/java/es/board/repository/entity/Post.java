package es.board.repository.entity;



import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "feed_id")
    private  String feedUID;

    private  String username;

    private  String title;

    private  boolean anonymous;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modify_at;

    @Column(name = "image_url")
    private  String imageUrl;




}
