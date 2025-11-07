package es.board.service.feed;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.mapper.PostDomainMapper;
import es.board.repository.entity.repository.infrastructure.feed.PostRepository;
import es.board.repository.entity.PostEntity;
import es.board.service.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;


    @Override
    public void savePost(PostDTO.Response feedSaveDTO) {

        Post post = PostDomainMapper.toDomain(feedSaveDTO);

        postRepository.savePost(Post.toEntity(post));
    }

    @Override
    public Page<PostEntity> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostEntity> byPagePosts = postRepository.findByPagePosts(pageable);
        return byPagePosts;
    }

    @Override
    public PostDTO.Request getPostDetail(long id) {

        PostEntity postDetail = postRepository.findPostDetail(id);
        Post post = Post.toDomain(postDetail);
        return  PostDomainMapper.toRequest(post);
    }
}
