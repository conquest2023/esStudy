package es.board.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.repository.document.Board;
import es.board.repository.document.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyRequest {

    private String id;

    private String commentUID;

    private String feedUID;

    private String username;


    private String content;

    private String category;

    private int likeCount;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;



    public List<ReplyRequest> ReplyListToDTO(List<Reply> reply) {
        return reply.stream()
                .map(reply1 -> ReplyRequest.builder()
                        .feedUID(reply1.getFeedUID())
                        .commentUID(reply1.getCommentUID())
                        .username(reply1.getUsername())
                        .content(reply1.getContent())
                        .likeCount(reply1.getLikeCount())
                        .createdAt(reply1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
