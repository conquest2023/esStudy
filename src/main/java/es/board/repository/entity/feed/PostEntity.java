package es.board.repository.entity.feed;



import es.board.repository.entity.poll.PollEntity;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Data
@Builder
//@ToString(exclude = "poll")
//@EqualsAndHashCode(exclude = "poll")
@AllArgsConstructor
@Slf4j
@RequiredArgsConstructor
public class PostEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "feed_id")
    private String feedUID;

    @Column(name = "user_id")
    private String userId;

    private  String username;

    private  String title;

    private String  description;

    private String category;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "view_count")
    private int viewCount;

    private  boolean anonymous;

    @Column(name = "image_url")
    private  String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

//    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL,
//            orphanRemoval = true,
//            fetch = FetchType.LAZY)
//    private PollEntity poll;

    public void applyFrom(String title,String description,LocalDateTime modifiedAt){
        this.title = title;
        this.description = description;
        this.modifiedAt = modifiedAt;
    }
}
