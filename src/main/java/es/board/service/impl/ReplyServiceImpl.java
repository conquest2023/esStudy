package es.board.service.impl;

import es.board.controller.model.dto.feed.ReplyDTO;
import es.board.controller.model.mapper.ReplyDomainMapper;
import es.board.repository.entity.ReplyEntity;
import es.board.repository.entity.repository.infrastructure.feed.ReplyRepository;
import es.board.service.ReplyService;
import es.board.service.domain.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository repository;
    @Override
    public void saveReply(String userId, ReplyDTO.Response response) {

        Reply reply = ReplyDomainMapper.toDomain(userId, response);
        ReplyEntity entity = Reply.toEntity(reply);
        repository.saveReply(entity);
    }
    @Override
    public List<ReplyDTO.Request> getReplys(int id) {

        List<ReplyEntity> byReplys = repository.findByReplys(id);
        List<Reply> repiles = Reply.toDomainList(byReplys);
        return  ReplyDomainMapper.toRequestDtoList(repiles);
    }
}
