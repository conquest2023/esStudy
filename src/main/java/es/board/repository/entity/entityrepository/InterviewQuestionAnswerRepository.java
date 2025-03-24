package es.board.repository.entity.entityrepository;

import es.board.repository.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionAnswerRepository  extends JpaRepository<InterviewAnswer ,Long> {


    @Query("SELECT s FROM InterviewAnswer s WHERE s.questionId IN :ids")
    List<InterviewAnswer> findInterviewAnswerByQuestion(@Param("ids") List<String> ids);

}
