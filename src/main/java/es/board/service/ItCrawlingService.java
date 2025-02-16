package es.board.service;

import es.board.controller.model.req.JobListing;
import es.board.controller.model.req.WantedJobData;

import java.util.List;
import java.util.Map;

public interface ItCrawlingService {

     List<Map<String, Object>> jumPitList();


     List<Map<String, Object>> programmersList();

     List<JobListing> jobPlanetList();

     List<WantedJobData> wantedList();

}
