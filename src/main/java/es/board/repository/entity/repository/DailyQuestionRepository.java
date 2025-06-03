package es.board.repository.entity.repository;

import es.board.repository.entity.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long> {



    @Query("select u FROM DailyQuestion u WHERE u.category =:category ORDER BY RAND() LIMIT 3")
    List<DailyQuestion> findDailyQuestionByToeic(@Param("category") String category);



    @Query("select u FROM DailyQuestion u WHERE u.subCategory = '일반행정' ORDER BY RAND() LIMIT 3")
    List<DailyQuestion> findDailyQuestionByCivil(String category);
}
