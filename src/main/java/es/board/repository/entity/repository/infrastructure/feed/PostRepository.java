package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.PostEntity;
import es.board.service.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PostRepository {


    String findByUserId(int id);

    void savePost(PostEntity post);

    Optional<PostEntity> findById(int id);
    PostEntity save(PostEntity post);

    void increaseViewCount(int postId);

    List<PostEntity> findByPosts(Pageable pageable);

    Page<PostEntity> findByPagePosts(Pageable pageable);

    PostEntity findPostDetail(int id);

    Page<Integer>  findIds(Pageable pageable);

    void deletePost(int id);


    Map<Integer, Long> countByReplyAndComment(List<Integer> ids);

}
