package es.board.repository.entity.entityrepository;


import es.board.repository.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {

    @Query(value = "(SELECT * FROM interview_questions WHERE category = 'IT' ORDER BY RAND() LIMIT 3) " +
            "UNION ALL " +
            "(SELECT * FROM interview_questions WHERE category = '일반' ORDER BY RAND() LIMIT 3)",
            nativeQuery = true)
    List<InterviewQuestion> findRandomITAndGeneralQuestions();

}
