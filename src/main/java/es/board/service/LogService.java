package es.board.service;

import es.board.controller.model.res.JobSiteLogDTO;

import java.util.List;

public interface LogService {



    void saveSiteLog(JobSiteLogDTO dto);


    List<JobSiteLogDTO> getSiteLog();
}
