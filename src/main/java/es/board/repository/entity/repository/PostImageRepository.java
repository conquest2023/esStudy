package es.board.repository.entity.repository;

import es.board.infrastructure.entity.feed.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {


    @Modifying
    @Query("UPDATE PostImage f SET f.postId = :postId WHERE f.imageUrl IN :urls")
    void updateFeedIdByImageUrls(@Param("postId") int postId, @Param("urls") List<String> urls);


    // 사용 여부 판단용
    boolean existsByImageUrl(String imageUrl);

}
