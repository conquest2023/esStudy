package es.board.service.feed;

import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.controller.model.mapper.ReplyDomainMapper;
import es.board.repository.entity.PostEntity;
import es.board.repository.entity.ReplyEntity;
import es.board.repository.entity.repository.infrastructure.feed.ReplyRepository;
import es.board.service.event.ReplyCreatedEvent;
import es.board.service.event.reply.ReplyEventListener;
import es.board.service.feed.ReplyService;
import es.board.service.domain.Reply;
import es.board.service.point.PointService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository repository;

    private final ReplyEventListener replyEventListener;
    @Override
    public void saveReply(String userId, ReplyDTO.Response response) {

        Reply reply = ReplyDomainMapper.toDomain(userId, response);
        ReplyEntity entity = Reply.toEntity(reply);
        repository.saveReply(entity);
        replyEventListener.handleReplyCreated(new ReplyCreatedEvent(response.getPostId(),userId,entity.getCommentId(),response));
    }
    @Override
    public List<ReplyDTO.Request> getReplys(String userId,int id) {

        List<ReplyEntity> byReplys = repository.findByReplys(id);
        List<Reply> replies = Reply.toDomainList(byReplys);
        return  ReplyDomainMapper.toRequestDtoList(userId,replies);
    }

    @Override
    @Transactional
    public ReplyDTO.Request updateReply(long id, ReplyDTO.Update update) {

        ReplyEntity replyEntity = repository.isExist(id).orElseThrow(() -> new EntityNotFoundException("Reply not found"));
        replyEntity.applyFrom(update.getContent());
        Reply reply = Reply.toDomain(replyEntity);

        return ReplyDomainMapper.toRequestDto(replyEntity.getUserId(),reply);
    }

    @Override
    public void deleteReply(long id) {
        repository.deleteReply(id);
    }
}
