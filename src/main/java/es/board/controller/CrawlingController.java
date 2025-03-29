package es.board.controller;


import es.board.controller.model.req.JobListing;
import es.board.controller.model.req.StudyTipDTO;
import es.board.controller.model.req.TistoryPost;
import es.board.controller.model.req.WantedJobData;
import es.board.repository.CertificateDAO;
import es.board.service.ItCrawlingService;
import es.board.service.impl.AsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CrawlingController {


    private final ItCrawlingService crawlingService;

    private  final CertificateDAO certificateDAO;


    private  final AsyncService asyncService;

    @GetMapping("/jumpit")
    public ResponseEntity<List<Map<String, Object>>> getJumPitList() {
        try {
            List<Map<String, Object>> jobList = crawlingService.jumPitList();
            return ResponseEntity.ok(jobList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/programmers")
    public ResponseEntity<List<Map<String, Object>>> getProgrammersList() {
        try {
            List<Map<String, Object>> jobList = crawlingService.programmersList();
            return ResponseEntity.ok(jobList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/jobplanet")
    public List<JobListing> getJobPlanetList() {
        return crawlingService.jobPlanetList();
    }
    @GetMapping("/wanted")
    public List<WantedJobData> getWantedList() {

        return crawlingService.wantedList();
    }

    @GetMapping("/naver/{keyword}")
    public ResponseEntity<List<StudyTipDTO>> getNaverTips(@PathVariable String keyword) {
        return ResponseEntity.ok(crawlingService.crawlNaverStudyTips(keyword));
    }

    @GetMapping("/google/{keyword}")
    public ResponseEntity<List<StudyTipDTO>> getGoogleTips(@PathVariable String keyword) {
        return ResponseEntity.ok(crawlingService.crawlGoogleStudyTips(keyword));
    }

//    @GetMapping("/tistory/{keyword}")
//    public CompletableFuture<ResponseEntity<List<TistoryPost>>> getTistory(@PathVariable String keyword) {
//        return crawlingService.crawlTistoryPosts(keyword)
//                .thenApply(ResponseEntity::ok);
//    }


//    @GetMapping("/ex/{keyword}")
//    public ResponseEntity<?> getGoogleTipdasd(@PathVariable String keyword) {
//        ResponseEntity<List<TistoryPost>> ok = ResponseEntity.ok(crawlingService.crawlTistoryPostEx(keyword));
//        return ResponseEntity.ok(ok);
//    }

    @GetMapping("/exss")
    public CompletableFuture<Void> getEx(String keyword) {
          crawlingService.crawlTistoryPosts("Sda");
          return  null;
    }
    @GetMapping("/gmarket")
    public ResponseEntity<?> getGmarket() {
        List<Map<String, Object>> ok = crawlingService.gmarketList();
        log.info(ok.toString());
        return ResponseEntity.ok(ok);
    }

}

