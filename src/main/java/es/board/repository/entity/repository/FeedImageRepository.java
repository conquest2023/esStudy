package es.board.repository.entity.repository;

import es.board.repository.entity.feed.FeedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {


    @Modifying
    @Query("UPDATE FeedImage f SET f.feedId = :feedId WHERE f.imageUrl IN :urls")
    void updateFeedIdByImageUrls(@Param("feedId") Long feedId, @Param("urls") List<String> urls);


    // 사용 여부 판단용
    boolean existsByImageUrl(String imageUrl);

    List<FeedImage> findAllByFeedId(Long feedId);

    List<FeedImage> findByFeedIdIsNullAndCreatedAtBefore(LocalDateTime cutoffTime);
}
