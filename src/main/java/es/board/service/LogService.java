package es.board.service;

import es.board.controller.model.dto.job.JobSiteLogDTO;

import java.util.List;

public interface LogService<T> {



    void saveLog(String indexName , T dto);


    List<JobSiteLogDTO> getSiteLog();
}
