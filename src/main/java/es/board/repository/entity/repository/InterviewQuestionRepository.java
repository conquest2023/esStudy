package es.board.repository.entity.repository;


import es.board.repository.entity.InterviewQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {

    @Query(value = "(SELECT * FROM interview_questions WHERE category = 'IT' ORDER BY RAND() LIMIT 3) " +
            "UNION ALL " +
            "(SELECT * FROM interview_questions WHERE category = '일반' ORDER BY RAND() LIMIT 3)",
            nativeQuery = true)
    List<InterviewQuestion> findRandomITAndGeneralQuestions();



    @Query("SELECT q FROM InterviewQuestion q WHERE q.category = :category AND q.subCategory = :subCategory")
    Page<InterviewQuestion> findByCategoryAndSubCategory(
            @Param("category") String category,
            @Param("subCategory") String subCategory,
            Pageable pageable
    );


    @Query("SELECT q FROM InterviewQuestion q WHERE q.id IN :ids")
    List<InterviewQuestion> findByIds(@Param("ids") List<String> ids);
}
