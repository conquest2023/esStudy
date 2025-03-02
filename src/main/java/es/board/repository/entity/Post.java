package es.board.repository.entity;



import es.board.controller.model.res.FeedCreateResponse;
import jakarta.persistence.*;
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



    public Post PostToEntity(FeedCreateResponse feedSaveDTO) {
        return Post.builder()
                .id(id)
                .userId(feedSaveDTO.getUserId())
                .feedUID(feedSaveDTO.getFeedUID())
                .username(feedSaveDTO.getUsername())
                .title(feedSaveDTO.getTitle())
                .imageUrl(feedSaveDTO.getImageURL())
                .anonymous(feedSaveDTO.isAnonymous())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
