package es.board.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedEvent implements Serializable {

    private String commentUID;

    private int postId;
    private String postOwnerId;

    private String commenterId;
    private String username;
    private String content;
    private LocalDateTime createdAt;

}
