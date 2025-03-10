package es.board.repository.entity.entityrepository;

import es.board.repository.entity.TistoryPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TistoryPostRepository extends JpaRepository<TistoryPost, Long> {

    @Modifying(clearAutomatically = true)  // 🚀 트랜잭션 clear 자동 실행
    @Transactional
    @Query(value = """
    INSERT INTO tistory_posts (name, blog_name, blog_url, title, post_url, thumbnail_url) 
    VALUES (:name, :blogName, :blogUrl, :title, :postUrl, :thumbnailUrl)
""", nativeQuery = true)
    void bulkInsert(
            @Param("name") String name,
            @Param("blogName") String blogName,
            @Param("blogUrl") String blogUrl,
            @Param("title") String title,
            @Param("postUrl") String postUrl,
            @Param("thumbnailUrl") String thumbnailUrl
    );
}
