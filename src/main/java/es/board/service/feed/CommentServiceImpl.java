package es.board.service.feed;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.mapper.CommentDomainMapper;
import es.board.repository.entity.feed.CommentEntity;
import es.board.repository.entity.repository.infrastructure.feed.CommentRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.service.domain.Comment;
import es.board.service.event.CommentCreatedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void saveComment(String userId,CommentDTO.Response res) {

        Comment comment = CommentDomainMapper.toDomain(userId,res, res.getPostId());
        CommentEntity entity = Comment.toEntity(comment);
        commentRepository.saveComment(entity);
        CommentCreatedEvent event = new CommentCreatedEvent(res.getPostId(),userId, res);
        eventPublisher.publishEvent(event);
    }

    @Override
    public List<CommentDTO.Request> getComments(String userId, int postId) {
        String postOwnerId = postRepository.findByUserId(postId);
        List<CommentEntity> byComments = commentRepository.findByComments(postId);
        List<Comment> comment = Comment.toDomainList(byComments);
        return CommentDomainMapper.toRequestDtoList(userId,postOwnerId,comment);
    }

    @Override
    public void getComment(int id) {
    }

    @Override
    @Transactional
    public CommentDTO.Request updateComment(long id,CommentDTO.Update update) {

        CommentEntity commentEntity = commentRepository.isExist(id).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        commentEntity.applyFrom(update.getContent());
        Comment comment = Comment.toDomain(commentEntity);
        return CommentDomainMapper.toUpdateDto(commentEntity.getUserId(),comment);
    }

    @Override
    public void deleteComment(long id) {
        commentRepository.deleteComment(id);
    }
}
