package es.board.service.poll;

import es.board.controller.model.dto.poll.PollDto;
import es.board.controller.model.dto.poll.PollVoteDTO;
import es.board.repository.entity.feed.PostEntity;
import es.board.repository.entity.poll.PollEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PollService {



    void createPoll(String username,String userId, PollDto.Request response);

    PollDto.Response getPollDetail(int postId);


    Page<PostEntity> getPollList(int page, int size);

    boolean isCheckVoteUser(long pollId, String userId);
    void vote(String userId, PollVoteDTO.Request request);

    void voteAll(String userId, PollVoteDTO.MultiRequest requests);
}
