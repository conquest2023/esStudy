package es.board.repository.entity.entityrepository;

import es.board.repository.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionAnswerRepository  extends JpaRepository<InterviewAnswer ,Long> {


}
