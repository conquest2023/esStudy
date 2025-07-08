package es.board.service.impl;

import es.board.controller.model.mapper.MainFunctionMapper;
import es.board.controller.model.dto.job.JobSiteLogDTO;
import es.board.repository.LogDAO;
import es.board.repository.document.JobSiteLog;
import es.board.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogServiceImpl<T> implements LogService<T> {


    private final MainFunctionMapper mapper;

    private final LogDAO logDAO;


    @Override
    public void saveLog(String indexName, T dto) {
        logDAO.saveLog(indexName,dto);
    }

    @Override
    public List<JobSiteLogDTO> getSiteLog() {

        List<JobSiteLog> top5 = logDAO.findJobSiteLog();
        return mapper.fromSiteList(top5);
    }
}
