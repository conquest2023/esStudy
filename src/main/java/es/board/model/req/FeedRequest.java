package es.board.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedRequest {

    private String id;

    private String feedUID;


    private String username;


    private String title;


    private String description;

    private String category;

    private int likeCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;




    public List<FeedRequest> BoardListToDTO(List<Board> boards) {
        return boards.stream()
                .map(board -> FeedRequest.builder()
                        .id(board.getFeedUID())
                        .username(board.getUsername())
                        .title(board.getTitle())
                        .description(board.getDescription())
                        .likeCount(board.getLikeCount())
                        .category(board.getCategory())
                        .createdAt(board.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    public FeedRequest BoardToDTO(Board board) {
            return FeedRequest.builder()

                    .feedUID(board.getFeedUID())
                    .username(board.getUsername())
                    .title(board.getTitle())
                    .description(board.getDescription())
                    .category(board.getCategory())
                    .createdAt(board.getCreatedAt())
                    .build();
        }
}
