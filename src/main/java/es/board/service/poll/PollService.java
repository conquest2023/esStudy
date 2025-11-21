package es.board.service.poll;

import es.board.controller.model.dto.poll.PollDto;
import es.board.controller.model.dto.poll.PollVoteDTO;

public interface PollService {



    void createPoll(String userId, PollDto.Request response);

    PollDto.Response getPollDetail(int postId);

    boolean isCheckVoteUser(long pollId, String userId);
    void vote(String userId, PollVoteDTO.Request request);
}
