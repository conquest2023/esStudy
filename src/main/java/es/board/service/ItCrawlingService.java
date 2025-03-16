package es.board.service;

import es.board.controller.model.req.JobListing;
import es.board.controller.model.req.StudyTipDTO;
import es.board.controller.model.req.TistoryPost;
import es.board.controller.model.req.WantedJobData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ItCrawlingService {

     List<Map<String, Object>> jumPitList();

//     CompletableFuture<Void> crawlTistoryPosts(String keyword);
     List<Map<String, Object>> programmersList();

     List<JobListing> jobPlanetList();

     List<WantedJobData> wantedList();

     List<StudyTipDTO> crawlNaverStudyTips(String keyword);

     List<StudyTipDTO> crawlGoogleStudyTips(String keyword);

     void crawlTistoryPosts(String keyword);

}
