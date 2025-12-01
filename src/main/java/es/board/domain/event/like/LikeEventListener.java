package es.board.domain.event.like;

import es.board.controller.model.mapper.LikeMapper;
import es.board.controller.record.LikeTargetInfo;
import es.board.domain.*;
import es.board.domain.enum_type.TargetType;
import es.board.domain.event.LikeCreatedEvent;
import es.board.infrastructure.entity.feed.CommentEntity;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.feed.ReplyEntity;
import es.board.infrastructure.entity.user.User;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventListener {

    private final NotificationService notificationService;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;

    private final LikeRepository likeRepository;

    private final NotificationRepository notificationRepository;


    @EventListener(LikeCreatedEvent.class)
    public void handleLikeCreated(LikeCreatedEvent event) {
        TargetType t = event.getRequest().getTargetType();

        LikeTargetInfo info = loadTargetInfo(t, event.getPostId(), event.getRequest().getTargetId());
        if (info == null) return;

        if (Objects.equals(info.ownerId(), event.getUserId()))
            return;

        User likeUser = likeRepository.findByLikeUser(event.getUserId(), t);
        String likerName = likeUser.getUsername();

        String tail = ellipsis(info.excerpt(), 40);
        String msg = switch (t) {
            case POST -> "%s님이 게시글에 좋아요를 누르셨습니다: %s".formatted(likerName, tail);
            case COMMENT -> "%s님이 댓글에 좋아요를 누르셨습니다: %s".formatted(likerName, tail);
            case REPLY -> "%s님이 답글에 좋아요를 누르셨습니다: %s".formatted(likerName, tail);
        };

        notificationService.sendLikeNotification(info.ownerId(), info.postId(), msg);

        notificationRepository.save(LikeMapper.toEntity(likerName,t,info,msg));
    }

    private LikeTargetInfo loadTargetInfo(TargetType t, int postId, long targetId) {
        return switch (t) {
            case POST -> postRepository.findById(postId)
                    .map(p -> new LikeTargetInfo(p.getUserId(), postId, p.getTitle()))
                    .orElse(null);

            case COMMENT -> commentRepository.findById(targetId)
                    .map(c -> new LikeTargetInfo(c.getUserId(), c.getPostId(), c.getContent()))
                    .orElse(null);

            case REPLY -> replyRepository.findById(targetId)
                    .map(r -> new LikeTargetInfo(r.getUserId(), r.getPostId(), r.getContent()))
                    .orElse(null);
        };
    }

    private String ellipsis(String s, int max) {
        if (s == null) return "";
        String trimmed = s.replaceAll("\\s+", " ").strip();
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max) + "…";
    }

}
