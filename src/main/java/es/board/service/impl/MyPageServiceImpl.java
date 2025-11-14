package es.board.service.impl;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.repository.entity.PostEntity;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.repository.entity.repository.infrastructure.feed.CommentRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostQueryRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.repository.infrastructure.projection.MyCommentProjection;
import es.board.repository.entity.repository.infrastructure.projection.PostsAndCommentsProjection;
import es.board.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {


    private final PostQueryRepository queryRepository;

    private final PointHistoryRepository pointHistoryRepository;

    private final CommentRepository commentRepository;
    @Override
    public int getSumPointUser(String userId) {
        return pointHistoryRepository.sumPointUser(userId);
    }

    @Override
    public  Page<PostEntity> getMyPageFeedList(int page, int size,String userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PostEntity> byMyPageUserPosts = queryRepository.findByMyPageUserPosts(pageable, userId);

        return byMyPageUserPosts;
    }

    @Override
    public Page<MyCommentProjection> getMyPageCommentList(int page, int size, String userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<MyCommentProjection> userMyPageComments = commentRepository.findUserMyPageComments(pageable, userId);
        return userMyPageComments;
    }

    @Override
    public Page<PostsAndCommentsProjection> getPostsCommentedByUser(int page, int size, String userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
         return commentRepository.findMyPagePostsAndComments(pageable,userId);
    }
}
