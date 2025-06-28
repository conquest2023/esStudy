package es.board.repository;

import es.board.repository.document.InterviewQuestion;

import java.util.List;

public interface InterviewQuestionDAO {

    List<InterviewQuestion> searchInterviewQuestion(String text);




}
