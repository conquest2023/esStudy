package es.board.service;

import es.board.controller.model.req.JobListing;
import es.board.controller.model.req.StudyTipRequest;
import es.board.controller.model.req.WantedJobData;

import java.util.List;
import java.util.Map;

public interface ItCrawlingService {

     List<Map<String, Object>> jumPitList();

//     CompletableFuture<Void> crawlTistoryPosts(String keyword);
     List<Map<String, Object>> programmersList();

     List<JobListing> jobPlanetList();


     List<Map<String, Object>> gmarketList();



     List<WantedJobData> wantedList();

     List<StudyTipRequest> crawlNaverStudyTips(String keyword);

     List<StudyTipRequest> crawlGoogleStudyTips(String keyword);

     void crawlTistoryPosts(String keyword);

}
