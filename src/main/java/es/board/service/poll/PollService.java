package es.board.service.poll;

import es.board.controller.model.dto.poll.PollDto;
import es.board.controller.model.dto.poll.PollVoteDTO;

import java.util.List;

public interface PollService {



    void createPoll(String username,String userId, PollDto.Request response);

    PollDto.Response getPollDetail(int postId);

    boolean isCheckVoteUser(long pollId, String userId);
    void vote(String userId, PollVoteDTO.Request request);

    void voteAll(String userId, PollVoteDTO.MultiRequest requests);
}
