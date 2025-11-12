package es.board.service.feed;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.mapper.PostDomainMapper;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.PostEntity;
import es.board.service.domain.Post;
import es.board.service.point.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{


    private final PointService pointService;

    private final PostRepository postRepository;


    @Override
    @Transactional
    public void incrementViewCount(int postId) {
         postRepository.increaseViewCount(postId);
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
        return postRepository.findIds(pageable);
    }

    @Override
    public Page<PostEntity> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostEntity> byPagePosts = postRepository.findByPagePosts(pageable);
        return byPagePosts;
    }

    @Override
    public PostDTO.Request getPostDetail(String userId,int id) {

        PostEntity postDetail = postRepository.findPostDetail(id);
        Post post = Post.toDomain(postDetail);
        return  PostDomainMapper.toRequest(userId,post);
    }

    @Override
    public Map<Integer, Long> getCountByCommentAndReply(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Integer> ids = postRepository.findIds(pageable);
        List<Integer> list = ids.getContent();
        Map<Integer, Long> map = postRepository.countByReplyAndComment(list);
        return map;
    }

    @Override
    public void deletePost(int id) {
        postRepository.deletePost(id);
    }

}
