package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface PostRepository {


    void savePost(PostEntity post);


    List<PostEntity> findByPosts(Pageable pageable);

    Page<PostEntity> findByPagePosts(Pageable pageable);

    PostEntity findPostDetail(int id);

    Page<Integer>  findIds(Pageable pageable);

    void deletePost(int id);


    Map<Integer, Long> countByReplyAndComment(List<Integer> ids);

}
