package es.board.repository;

import es.board.repository.document.InterviewLog;
import es.board.repository.document.JobSiteLog;

import java.util.List;

public interface LogDAO<T> {


    void saveLog(String indexName, T dto);

    List<String> aggregationInterviewQuestionLog(String subCategory);
    List<JobSiteLog> findJobSiteLog();
}
