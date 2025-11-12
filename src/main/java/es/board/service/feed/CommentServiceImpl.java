package es.board.service.feed;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.mapper.CommentDomainMapper;
import es.board.repository.entity.CommentEntity;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.repository.entity.repository.infrastructure.feed.CommentRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.service.NotificationService;
import es.board.service.domain.Comment;
import es.board.service.event.CommentCreatedEvent;
import es.board.service.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void saveComment(String userId,CommentDTO.Response res) {

        Comment comment = CommentDomainMapper.toDomain(userId,res, res.getPostId());
        CommentEntity entity = Comment.toEntity(comment);
        repository.saveComment(entity);
//        if (userId!= null && !userId.equals(res.getUserId())) {
//            notificationService.sendCommentNotification(userId, res.getPostId(),
//                    res.getUsername() + "님이 댓글을 작성하였습니다: " + res.getContent());
////            notificationRepository.save(CommentDomainMapper.toEntityNotification(postUserId,res));
//        }
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
    public void deleteComment(long id) {
        repository.deleteComment(id);
    }
}
