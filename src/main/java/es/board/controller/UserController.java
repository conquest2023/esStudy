package es.board.controller;

import es.board.controller.model.dto.feed.TopWriter;
import es.board.controller.model.dto.feed.SignUpDTO;
import es.board.service.AuthService;
import es.board.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {



    private final AuthService userService;

    private final PointService pointService;


    @GetMapping("/points/summary")
    public ResponseEntity<?> getTop5UserPoint() {

        List<TopWriter> sumPoint = pointService.getSumTop5User();
        return ResponseEntity.ok(Map.of("top5",sumPoint));
    }
    @GetMapping("/points/recent")
    public ResponseEntity<?> getTop5UserRecentPoint() {

        List<TopWriter> sumPoint = pointService.getSumTop5RecentUser();
        return ResponseEntity.ok(Map.of("recent",sumPoint));
    }


    @PostMapping("/check")
    public ResponseEntity<Boolean> checkUserId(@RequestBody SignUpDTO sign) {
        boolean isAvailable = userService.checkId(sign);
        return ResponseEntity.ok(isAvailable);
    }


}
