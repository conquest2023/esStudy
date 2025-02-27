package es.board.controller;


import es.board.controller.model.req.JobListing;
import es.board.controller.model.req.WantedJobData;
import es.board.service.ItCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CrawlingController {


    private final ItCrawlingService crawlingService;

    @GetMapping("/jumpit")
    public ResponseEntity<List<Map<String, Object>>> getJumPitList() {
        try {
            List<Map<String, Object>> jobList = crawlingService.jumPitList();
            return ResponseEntity.ok(jobList); // 200 OK 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/programmers")
    public ResponseEntity<List<Map<String, Object>>> getProgrammersList() {
        try {
            List<Map<String, Object>> jobList = crawlingService.programmersList();
            return ResponseEntity.ok(jobList); // 200 OK 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //    @GetMapping("/jobplanet")
//    public List<JobListing> getJobPlanetList() {
//        return crawlingService.jobPlanetList();
//    }
//
//
    @GetMapping("/wanted")
    public List<WantedJobData> getWantedList() {

        return crawlingService.wantedList();
    }
}

