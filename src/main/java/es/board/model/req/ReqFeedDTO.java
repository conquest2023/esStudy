package es.board.model.req;

import es.board.repository.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqFeedDTO {

    private String id;


    private String username;


    private String title;


    private String description;


    public List<ReqFeedDTO> entityToDTO(List<Board> boards) {
        return boards.stream()
                .map(board -> ReqFeedDTO.builder()
                        .id(board.getId())
                        .username(board.getUsername())
                        .title(board.getTitle())
                        .description(board.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
