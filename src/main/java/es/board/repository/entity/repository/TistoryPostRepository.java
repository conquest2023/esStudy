package es.board.repository.entity.repository;

import es.board.repository.entity.TistoryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TistoryPostRepository extends JpaRepository<TistoryPost, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
    INSERT INTO tistory_posts (name, blog_name, blog_url, title, post_url, thumbnail_url) 
    VALUES (:#{#posts[0].name}, :#{#posts[0].blogName}, :#{#posts[0].blogUrl}, :#{#posts[0].title}, :#{#posts[0].postUrl}, :#{#posts[0].thumbnailUrl})
    , (:#{#posts[1].name}, :#{#posts[1].blogName}, :#{#posts[1].blogUrl}, :#{#posts[1].title}, :#{#posts[1].postUrl}, :#{#posts[1].thumbnailUrl})
    , (:#{#posts[2].name}, :#{#posts[2].blogName}, :#{#posts[2].blogUrl}, :#{#posts[2].title}, :#{#posts[2].postUrl}, :#{#posts[2].thumbnailUrl})
""", nativeQuery = true)
    void bulkInsertBatch(@Param("posts") List<TistoryPost> posts);

}
