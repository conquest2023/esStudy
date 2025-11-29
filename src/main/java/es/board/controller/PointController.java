//package es.board.controller;
//
//import es.board.controller.model.dto.feed.TopWriter;
//import es.board.domain.point.PointService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//import java.util.Map;
//@Slf4j
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class PointController {
//
//    private final PointService pointService;
//
//    @GetMapping("/points/summary")
//    public ResponseEntity<?> getTop5UserPoint() {
//
//        List<TopWriter> sumPoint = pointService.getSumTop5User();
//        return ResponseEntity.ok(Map.of("top5",sumPoint));
//    }
//    @GetMapping("/points/recent")
//    public ResponseEntity<?> getTop5UserRecentPoint() {
//
//        List<TopWriter> sumPoint = pointService.getSumTop5RecentUser();
//        return ResponseEntity.ok(Map.of("recent",sumPoint));
//    }
//}
