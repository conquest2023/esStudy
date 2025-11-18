package es.board.service.feed;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.mapper.PostDomainMapper;
import es.board.repository.entity.repository.infrastructure.feed.PostQueryRepository;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.PostEntity;
import es.board.service.domain.Post;
import es.board.service.point.PointService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{


    private final PointService pointService;

    private final PostRepository postRepository;

    private final PostQueryRepository queryRepository;


    @Override
    @Transactional
    public void incrementViewCount(int postId) {
         postRepository.increaseViewCount(postId);
    }

    @Override
    public Page<PostEntity> findPopularPostsInLast7Weeks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSevenDay = now.minusDays(7);
        Page<PostEntity> byMyPageUserPosts = queryRepository.findPopularPostsInLast7Week(pageable, lastSevenDay);
        return byMyPageUserPosts;
    }

    @Override
    @Transactional
    public PostDTO.Request updatePost(int id,String userId, PostDTO.Update update) {

        PostEntity postEntity = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        postEntity.applyFrom(update.getTitle(),update.getDescription());
        Post domain = Post.toDomain(postEntity);
        return PostDomainMapper.toRequest(postEntity.getUserId(),domain);
    }

    @Override
    public void savePost(String userId, PostDTO.Response feedSaveDTO) {

        Post post = PostDomainMapper.toDomain(userId,feedSaveDTO);
        postRepository.savePost(Post.toEntity(post));
        pointService.grantActivityPoint(userId,"피드",3,5);
    }

    @Override
    public Page<Integer> findIds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return queryRepository.findIds(pageable);
    }

    @Override
    public Page<PostEntity> findAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostEntity> byPagePosts = queryRepository.findByPagePosts(pageable);
        return byPagePosts;
    }

    @Override
    public PostDTO.Request findPostDetail(String userId, int id) {

        PostEntity postDetail = queryRepository.findPostDetail(id);
        Post post = Post.toDomain(postDetail);
        return  PostDomainMapper.toRequest(userId,post);
    }

    @Override
    public Map<Integer, Long> getCountByCommentAndReply(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Integer> ids = queryRepository.findPostIds(pageable);
        Map<Integer, Long> map = queryRepository.countByReplyAndComment(ids);
        return map;
    }

    @Override
    public void deletePost(int id) {
        postRepository.deletePost(id);
    }

}
