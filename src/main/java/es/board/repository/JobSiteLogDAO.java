package es.board.repository;

import es.board.controller.model.res.JobSiteLogDTO;
import es.board.repository.document.JobSiteLog;

import java.util.List;

public interface JobSiteLogDAO {


    void saveJobSiteLog(JobSiteLogDTO dto);


    List<JobSiteLog> findJobSiteLog();
}
