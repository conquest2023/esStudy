package es.board.repository.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "likes")
public class Likes {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String  username;


    @Column(name = "user_id")
    private String  userId;


    @Column(name = "feed_id")
    private  String  feedUID;


    private LocalDateTime created_at;


}
