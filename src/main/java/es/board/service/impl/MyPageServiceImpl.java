package es.board.service.impl;

import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.dto.feed.PostDTO;
import es.board.repository.entity.PostEntity;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.repository.entity.repository.infrastructure.feed.CommentRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
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

    private final PostRepository postRepository;

    private final PointHistoryRepository pointHistoryRepository;

    private final CommentRepository commentRepository;
    @Override
    public int getSumPointUser(String userId) {
        return pointHistoryRepository.sumPointUser(userId);
    }

    @Override
    public  Page<PostEntity> getMyPageFeedList(int page, int size,String userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<PostEntity> byMyPageUserPosts = postRepository.findByMyPageUserPosts(pageable, userId);

        return byMyPageUserPosts;
    }

    @Override
    public Page<CommentDTO.Request> getMyPageCommentList(String userId) {
        return null;
    }

    @Override
    public Page<PostDTO.Request> getPostsCommentedByUser(String userId) {
        return null;
    }
}
