package es.board.repository.entity;


import es.board.config.s3.S3Uploader;
import es.board.controller.model.res.FeedCreateResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post")
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private  String username;

    private  String title;

    private  boolean anonymous;

    @Column(name = "user_id")
    private String userId;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modify_at;

    @Column(name = "image_url")
    private  String imageUrl;


    public Post PostToEntity(FeedCreateResponse feedSaveDTO) {
        return Post.builder()
                .id(id)
                .userId(feedSaveDTO.getUserId())
                .username(feedSaveDTO.getUsername())
                .title(feedSaveDTO.getTitle())
                .imageUrl(feedSaveDTO.getImageURL())
                .anonymous(feedSaveDTO.isAnonymous())
                .createdAt(LocalDateTime.now())
                .build();
    }




}
