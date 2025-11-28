package es.board.service.impl;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.domain.CommentRepository;
import es.board.infrastructure.feed.PostQueryRepository;
import es.board.infrastructure.projection.MyCommentProjection;
import es.board.infrastructure.projection.PostsAndCommentsProjection;
import es.board.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
