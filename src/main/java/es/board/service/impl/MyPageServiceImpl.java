package es.board.service.impl;
import es.board.controller.model.dto.UserPoint;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.entity.user.User;
import es.board.infrastructure.jpa.projection.LikeCountPostProjection;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.domain.CommentRepository;
import es.board.infrastructure.feed.PostQueryRepository;
import es.board.infrastructure.jpa.projection.MyCommentProjection;
import es.board.infrastructure.jpa.projection.PostsAndCommentsProjection;
import es.board.repository.entity.repository.UserRepository;
import es.board.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;
    private final PostQueryRepository queryRepository;

    private final PointHistoryRepository pointHistoryRepository;

    private final CommentRepository commentRepository;
    @Override
    public UserPoint  getSumPointUser(String userId) {

        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            log.warn("User not found for userId: {}", userId);
            throw  new RuntimeException("유저를 찾지 못했습니다.");
        }
        int userPoint = pointHistoryRepository.sumPointUser(userId);
        int likePoint = pointHistoryRepository.countUserLikes(userId);

       return UserPoint.builder()
                .userPoint(userPoint+likePoint* 10L)
                .likeCount(likePoint)
                .build();
    }

    @Override
    public  Page<PostEntity> getMyPageFeedList(int page, int size,String userId) {

        Page<PostEntity> byMyPageUserPosts = queryRepository.findByMyPageUserPosts(page,size, userId);

        return byMyPageUserPosts;
    }

    @Override
    public Page<MyCommentProjection> getMyPageCommentList(int page, int size, String userId) {

        Page<MyCommentProjection> userMyPageComments = commentRepository.findUserMyPageComments(page,size, userId);
        return userMyPageComments;
    }

    @Override
    public Page<PostsAndCommentsProjection> getPostsCommentedByUser(int page, int size, String userId) {
         return commentRepository.findMyPagePostsAndComments(page,size,userId);
    }
}
