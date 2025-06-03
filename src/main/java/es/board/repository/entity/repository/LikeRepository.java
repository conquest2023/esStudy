package es.board.repository.entity.repository;


import es.board.repository.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Likes,Long> {



//    @Query("select  f.userId  from Likes f where f.feedUID=:feedId and  f.userId=:userId")
    boolean existsByUserIdAndFeedUID(String userId, String feedUID);
    @Modifying
    @Query("DELETE FROM Likes f WHERE f.userId = :user_id AND f.feedUID = :feed_id")
    void deleteByUserIdAndFeedUID(@Param("user_id") String userId, @Param("feed_id") String feedUID);
}
