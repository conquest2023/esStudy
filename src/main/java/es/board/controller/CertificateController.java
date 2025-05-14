package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.repository.document.Certificate;
import es.board.service.CertificateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api")

public class CertificateController {


    private  final JwtTokenProvider jwtTokenProvider;

    private  final CertificateService certificateService;


    @GetMapping("/certificate/list")
    public String certificateList(){
        return  "basic/feed/Certificate";
    }

    @GetMapping("/certificate/top5")
    @ResponseBody
    public ResponseEntity<?> getCertificate(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("certificateTop5", certificateService.getTop5CertificateCount());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/certificate/schedule/{text}")
    @ResponseBody
    public ResponseEntity<?> getScheduleCertificate(@RequestHeader(value = "Authorization", required = false) String token,
                                                    @PathVariable String text) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("certSchedule", certificateService.getCertificationSchedule(text));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/certificate")
    @ResponseBody
    public ResponseEntity<?> getSearchCertificate(@RequestHeader(value = "Authorization", required = false) String token,
                                                  @RequestParam String text
                                         , HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }
        List<Certificate> certificate =certificateService.getCertificate(text,clientIp);
        Map<String, Object> response = new HashMap<>();
        response.put("certificate", certificate);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/certificate/category/{mainCategory}/{subCategory}")
    @ResponseBody
    public ResponseEntity<?> getMainCategoryAndSubCategory(@RequestHeader(value = "Authorization", required = false) String token,
                                                    @PathVariable String mainCategory ,@PathVariable String subCategory ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "토큰이 필요합니다."));
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "세션이 만료되었습니다."));
        }

        Map<String, Object> response = new HashMap<>();

        List<String> cert=certificateService.getMainCategoryAndSubCategory(mainCategory,subCategory);
        response.put("certSchedule", cert);

        return ResponseEntity.ok(response);
    }
}
