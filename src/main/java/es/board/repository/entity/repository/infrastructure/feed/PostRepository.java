package es.board.repository.entity.repository.infrastructure.feed;

import es.board.repository.entity.PostEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository {


    String findByUserId(int id);

    void savePost(PostEntity post);
    void deletePost(int id);

    void increaseViewCount(int postId);

    PostEntity save(PostEntity post);
    Optional<PostEntity> findById(int id);



}
