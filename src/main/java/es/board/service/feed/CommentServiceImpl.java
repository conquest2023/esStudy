package es.board.service.feed;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.mapper.CommentDomainMapper;
import es.board.controller.model.mapper.CommentMapper;
import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.repository.entity.repository.infrastructure.feed.CommentRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.service.NotificationService;
import es.board.service.domain.Comment;
import es.board.service.event.CommentCreatedEvent;
import es.board.service.point.PointService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void saveComment(String userId,CommentDTO.Response res) {

        Comment comment = CommentDomainMapper.toDomain(userId,res, res.getPostId());
        CommentEntity entity = Comment.toEntity(comment);
        repository.saveComment(entity);
        CommentCreatedEvent event = new CommentCreatedEvent(res.getPostId(),userId, res);
        eventPublisher.publishEvent(event);
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
    @Transactional
    public CommentDTO.Request updateComment(long id,CommentDTO.Update update) {

        CommentEntity commentEntity = repository.isExist(id).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        commentEntity.applyFrom(update.getContent());
        Comment comment = Comment.toDomain(commentEntity);
        return CommentDomainMapper.toRequestDto(commentEntity.getUserId(),comment);
    }

    @Override
    public void deleteComment(long id) {
        repository.deleteComment(id);
    }
}
