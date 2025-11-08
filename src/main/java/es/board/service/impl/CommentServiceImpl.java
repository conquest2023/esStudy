package es.board.service.impl;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.mapper.CommentDomainMapper;
import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.repository.infrastructure.feed.CommentRepository;
import es.board.service.CommentService;
import es.board.service.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;

    @Override
    public void saveComment(String userId,CommentDTO.Response res) {

        Comment comment = CommentDomainMapper.toDomain(userId,res, res.getPostId());
        CommentEntity entity = Comment.toEntity(comment);
        repository.saveComment(entity);
    }

    @Override
    public List<CommentDTO.Request> getComments(String userId, int id) {

        List<CommentEntity> byComments = repository.findByComments(id);
        List<Comment> comment = Comment.toDomainList(byComments);
        return CommentDomainMapper.toRequestDtoList(userId,comment);
    }

    @Override
    public void getComment(int id) {
    }

    @Override
    public void deleteComment(long id) {
        repository.deleteComment(id);
    }
}
