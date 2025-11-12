package es.board.service.feed;

import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.controller.model.mapper.ReplyDomainMapper;
import es.board.repository.entity.ReplyEntity;
import es.board.repository.entity.repository.infrastructure.feed.ReplyRepository;
import es.board.service.feed.ReplyService;
import es.board.service.domain.Reply;
import es.board.service.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository repository;

    private final PointService pointService;
    @Override
    public void saveReply(String userId, ReplyDTO.Response response) {

        Reply reply = ReplyDomainMapper.toDomain(userId, response);
        ReplyEntity entity = Reply.toEntity(reply);
        repository.saveReply(entity);
        pointService.grantActivityPoint(userId,"답글",3,10);
    }
    @Override
    public List<ReplyDTO.Request> getReplys(String userId,int id) {

        List<ReplyEntity> byReplys = repository.findByReplys(id);
        List<Reply> replies = Reply.toDomainList(byReplys);
        return  ReplyDomainMapper.toRequestDtoList(userId,replies);
    }

    @Override
    public void deleteReply(long id) {
        repository.deleteReply(id);
    }
}
