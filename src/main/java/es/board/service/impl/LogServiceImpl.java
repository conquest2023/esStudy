package es.board.service.impl;

import es.board.controller.model.mapper.MainFunctionMapper;
import es.board.controller.model.res.JobSiteLogDTO;
import es.board.repository.JobSiteLogDAO;
import es.board.repository.document.JobSiteLog;
import es.board.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogServiceImpl implements LogService {


    private final MainFunctionMapper mapper;


    private final JobSiteLogDAO jobSiteLogDAO;

    @Override
    public void saveSiteLog(JobSiteLogDTO dto) {
        jobSiteLogDAO.saveJobSiteLog(dto);
    }

    @Override
    public List<JobSiteLogDTO> getSiteLog() {

        List<JobSiteLog> top5 = jobSiteLogDAO.findJobSiteLog();
        return mapper.fromSiteList(top5);
    }
}
