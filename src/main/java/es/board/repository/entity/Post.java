package es.board.repository.entity;


import es.board.controller.model.res.FeedCreateResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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


    public Post PostToEntity(FeedCreateResponse feedSaveDTO) {
        return Post.builder()
                .id(id)
                .userId(feedSaveDTO.getUserId())
                .username(feedSaveDTO.getUsername())
                .title(feedSaveDTO.getTitle())
                .anonymous(feedSaveDTO.isAnonymous())
                .createdAt(LocalDateTime.now())
                .build();
    }


}
