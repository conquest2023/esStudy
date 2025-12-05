package es.board.infrastructure.jpa;

import es.board.infrastructure.entity.user.User;
import es.board.infrastructure.feed.LikeAggView;
import es.board.infrastructure.entity.feed.LikeEntity;
import es.board.infrastructure.projection.LikeCountProjection;
import es.board.domain.enum_type.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeJpaRepository extends JpaRepository<LikeEntity,Integer> {


    @Query("select  l.postId as postId, count(l) as cnt from LikeEntity l " +
            "where l.postId in :id" +
            " group by postId ")
    List<LikeAggView> findPagingLikes(List<Integer> id);

    @Query("select u from User u inner join LikeEntity l on l.userId=u.userId" +
            " where u.userId=:userId and l.targetType=:targetType")
    User findByLikeUser(@Param("userId") String userId,
                        @Param("targetType") TargetType targetType);

    @Query("SELECT l FROM LikeEntity l WHERE l.userId = :userId" +
            " AND l.postId = :postId AND l.targetId=:targetId")
    Optional<LikeEntity> countByUserIdAndPostId(String userId, int postId , long targetId);
    @Modifying
    @Query("delete from LikeEntity l where  l.id=:id")
    void deleteById(long id);

    @Query("select l from LikeEntity l " +
            "where l.postId=:id")
    List<LikeEntity> findByFeedDetail(int id);

    @Query("""
    select l.targetId as targetId,
           count(l) as count
    from LikeEntity l  where l.postId = :id group by l.targetId""")
    List<LikeCountProjection> countByPostGroupByTargetType(@Param("id") int id);

}
