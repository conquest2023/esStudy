package es.board.domain.poll;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.poll.PollDto;
import es.board.controller.model.dto.poll.PollVoteDTO;
import es.board.controller.model.mapper.PollDomainMapper;
import es.board.controller.model.mapper.PostDomainMapper;
import es.board.domain.event.PollCreatedEvent;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.poll.PollEntity;
import es.board.infrastructure.entity.poll.PollOptionEntity;
import es.board.infrastructure.entity.poll.PollVoteEntity;
import es.board.repository.entity.repository.PostRepository;
import es.board.infrastructure.poll.PollOptionRepository;
import es.board.infrastructure.poll.PollRepository;
import es.board.infrastructure.poll.PollVoteRepository;
import es.board.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createPoll(String username, String userId, PollDto.Request req) {

        Post post = PostDomainMapper.toDomain(userId, new PostDTO.Request(
                username,
                req.getTitle(),
                req.getDescription(),
                "투표"));
        PostEntity entity = Post.toEntity(post);
        PollEntity poll = PollDomainMapper.toEntity(entity, req);
        PostEntity postId = postRepository.save(entity);
        pollRepository.save(poll);
        eventPublisher.publishEvent(new PollCreatedEvent(postId.getId(),userId,req));
    }

    @Override
    public PollDto.Response getPollDetail(int postId) {
        PollEntity poll = pollRepository.findPollDetail(postId);
        return  PollDomainMapper.toPollRequest(poll);
    }

    @Override
    public Page<PostEntity> getPollList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return pollRepository.findPollPagingList(pageable);

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

    @Override
    public void voteAll(String userId, PollVoteDTO.MultiRequest requests) {
        PollEntity poll = pollRepository.findById(requests.getPollId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표입니다."));
        List<PollOptionEntity> options = pollOptionRepository.isCheckOptionList(requests.getPollId(),requests.getOptionIds())
                .orElseThrow(() -> new IllegalArgumentException("해당 투표에 없는 옵션입니다."));
        List<PollVoteEntity>  vote = PollDomainMapper.toVoteRequestList(requests, userId, poll, options);
        pollVoteRepository.voteAll(vote);
    }

}
