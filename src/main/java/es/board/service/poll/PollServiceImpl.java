package es.board.service.poll;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.poll.PollDto;
import es.board.controller.model.dto.poll.PollVoteDTO;
import es.board.controller.model.mapper.PollDomainMapper;
import es.board.controller.model.mapper.PostDomainMapper;
import es.board.repository.entity.feed.PostEntity;
import es.board.repository.entity.poll.PollEntity;
import es.board.repository.entity.poll.PollOptionEntity;
import es.board.repository.entity.poll.PollVoteEntity;
import es.board.repository.entity.repository.PostRepository;
import es.board.repository.entity.repository.infrastructure.poll.PollOptionRepository;
import es.board.repository.entity.repository.infrastructure.poll.PollRepository;
import es.board.repository.entity.repository.infrastructure.poll.PollVoteRepository;
import es.board.service.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PollServiceImpl implements PollService{

    private final PostRepository postRepository;

    private final PollRepository pollRepository;

    private final PollVoteRepository pollVoteRepository;

    private final PollOptionRepository pollOptionRepository;
    @Override
    @Transactional
    public void createPoll(String userId, PollDto.Request res) {

        Post post = PostDomainMapper.toDomain(userId, new PostDTO.Response(
                userId,
                res.getCategory(),
                res.getTitle(),
                res.getDescription()));

        PostEntity entity = Post.toEntity(post);

        PollEntity poll = PollDomainMapper.toEntity(entity, res);
        postRepository.save(entity);
        pollRepository.save(poll);
    }

    @Override
    public PollDto.Response getPollDetail(int postId) {
        PollEntity poll = pollRepository.findPollDetail(postId);
        return  PollDomainMapper.toPollRequest(poll);
    }

    @Override
    public boolean isCheckVoteUser(long pollId, String userId) {
        Set<String> voters = pollVoteRepository.findVoters(pollId);
        return voters.contains(userId);
    }

    @Override
    public void vote(String userId, PollVoteDTO.Request request) {

        PollEntity poll = pollRepository.findById(request.getPollId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));
        PollOptionEntity option = pollOptionRepository.isCheckOption(request.getOptionId(), request.getPollId())
                .orElseThrow(() -> new IllegalArgumentException("해당 투표에 없는 옵션입니다."));
        PollVoteEntity vote = PollDomainMapper.toVoteRequest(request,userId, poll, option);

        pollVoteRepository.vote(vote);
    }

}
