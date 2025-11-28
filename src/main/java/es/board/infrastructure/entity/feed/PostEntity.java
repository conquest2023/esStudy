package es.board.infrastructure.entity.feed;


import com.fasterxml.jackson.annotation.JsonIgnore;
import es.board.infrastructure.entity.poll.PollEntity;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "post")
@Getter
@Builder
@ToString(exclude = "poll")
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

//    @Column(name = "feed_id")
//    private String feedUID;


    @Column(name = "user_id")
    private String userId;

    private  String username;

    private  String title;

    private String  description;

    private String category;


    @Column(name = "view_count")
    private int viewCount;

    private  boolean anonymous;



    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;


//    @OneToOne(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL,
//            fetch = FetchType.LAZY)
//    @JsonIgnore
//    private PollEntity poll;


    public void applyFrom(String title,String description,LocalDateTime modifiedAt){
        this.title = title;
        this.description = description;
        this.modifiedAt = modifiedAt;
    }

}
