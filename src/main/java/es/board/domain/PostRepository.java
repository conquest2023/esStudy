package es.board.domain;

import es.board.infrastructure.entity.feed.PostEntity;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository {


    String findByUserId(int id);


    PostEntity savePost(PostEntity post);
    void deletePost(int id);

    void increaseViewCount(int postId);

    PostEntity save(PostEntity post);
    Optional<PostEntity> findById(int id);



}
