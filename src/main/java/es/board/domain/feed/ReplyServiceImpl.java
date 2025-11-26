package es.board.domain.feed;

import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.controller.model.mapper.ReplyDomainMapper;
import es.board.infrastructure.entity.feed.ReplyEntity;
import es.board.domain.PostRepository;
import es.board.domain.ReplyRepository;
import es.board.domain.event.ReplyCreatedEvent;
import es.board.domain.event.reply.ReplyEventListener;
import es.board.domain.Reply;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;

    private final PostRepository postRepository;

    private final ReplyEventListener replyEventListener;
    @Override
    public void saveReply(String userId, ReplyDTO.Response response) {

        Reply reply = ReplyDomainMapper.toDomain(userId, response);
        ReplyEntity entity = Reply.toEntity(reply);
        replyRepository.saveReply(entity);
        replyEventListener.handleReplyCreated(new ReplyCreatedEvent(response.getPostId(),userId,entity.getCommentId(),response));
    }
    @Override
    public List<ReplyDTO.Request> getReplys(String userId,int postId) {
        String postOwnerId = postRepository.findByUserId(postId);
        List<ReplyEntity> byReplies = replyRepository.findByReplys(postId);
        List<Reply> replies = Reply.toDomainList(byReplies);
        return  ReplyDomainMapper.toRequestDtoList(userId,postOwnerId,replies);
    }

    @Override
    @Transactional
    public ReplyDTO.Request updateReply(long id, ReplyDTO.Update update) {
        ReplyEntity replyEntity = replyRepository.isExist(id).orElseThrow(() -> new EntityNotFoundException("Reply not found"));
        replyEntity.applyFrom(update.getContent());
        Reply reply = Reply.toDomain(replyEntity);
        return ReplyDomainMapper.toUpdateDto(replyEntity.getUserId(),reply);
    }

    @Override
    public void deleteReply(long id) {
        replyRepository.deleteReply(id);
    }
}
