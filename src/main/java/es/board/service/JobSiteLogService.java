package es.board.service;

import es.board.controller.model.res.JobSiteLogDTO;

import java.util.List;

public interface JobSiteLogService {



    void saveSiteLog(JobSiteLogDTO dto);


    List<JobSiteLogDTO> getSiteLog();
}
