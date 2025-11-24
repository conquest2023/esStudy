package es.board.controller.model.dto;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.poll.PollDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailResponse {

    private PostDTO.Response post;

    private PollDto.Response poll;

    private boolean hasPoll;
}