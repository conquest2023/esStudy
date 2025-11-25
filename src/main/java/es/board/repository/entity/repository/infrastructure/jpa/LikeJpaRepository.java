package es.board.repository.entity.repository.infrastructure.jpa;

import es.board.repository.entity.feed.LikeEntity;
import es.board.repository.entity.repository.infrastructure.feed.LikeAggView;
import es.board.repository.entity.repository.infrastructure.projection.LikeCountProjection;
import es.board.service.domain.enum_type.TargetType;
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

    @Query("SELECT l FROM LikeEntity l WHERE l.userId = :userId" +
            " AND l.postId = :postId AND l.targetType=:targetType")
    Optional<LikeEntity> countByUserIdAndPostId(String userId, int postId , TargetType targetType);
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
