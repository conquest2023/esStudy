package es.board.repository.entity.repository;

import es.board.repository.entity.Bookmark;
import es.board.repository.entity.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookMarkRepository extends JpaRepository<Bookmark, Long> {


//    @Query("""
//        SELECT b.dailyQuestion
//        FROM Bookmark b
//        JOIN FETCH b.dailyQuestion
//        WHERE b.user.userId = :userId
//        ORDER BY b.createdAt DESC
//    """)
//    List<DailyQuestion> findBookmarkedQuestionsByUserId(@Param("userId") String userId);
    @Query("SELECT b.dailyQuestion FROM Bookmark b WHERE b.user.userId = :userId")
    List<DailyQuestion> findBookmarkedQuestionsByUserId(@Param("userId") String userId);

}
