package es.board.repository.entity.repository;


import es.board.repository.entity.QuestionPractical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionPracticalRepository extends JpaRepository<QuestionPractical,Long> {

    @Query("select u from QuestionPractical u where u.category = :category and  u.type=:type")
    List<QuestionPractical> findByPracticalCategory(@Param("category") String category, @Param("type") String type);


    @Query(value = "SELECT * FROM question_practical ORDER BY RAND() LIMIT 20", nativeQuery = true)
    List<QuestionPractical> findRandomPracticalCategory();


    @Query("""
        SELECT qp FROM QuestionPractical qp
        JOIN qp.tags t
        WHERE t.tagId = :tagId
    """)
    List<QuestionPractical> findByTagId(@Param("tagId") Long tagId);



}
