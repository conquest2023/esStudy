package es.board.controller;


import es.board.config.jwt.JwtTokenProvider;
import es.board.ex.TokenValidator;
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



    private  final TokenValidator tokenValidator;

    private  final CertificateService certificateService;


    @GetMapping("/certificate/list")
    public String certificateList(){
        return  "basic/feed/Certificate";
    }

    @GetMapping("/certificate/top5")
    @ResponseBody
    public ResponseEntity<?> getCertificate(@RequestHeader(value = "Authorization", required = false) String token) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse != null) {
            return tokenCheckResponse;
        }
        return ResponseEntity.ok(Map.of("certificateTop5",certificateService.getTop5CertificateCount()));
    }
    @GetMapping("/certificate/schedule/{text}")
    @ResponseBody
    public ResponseEntity<?> getScheduleCertificate(@RequestHeader(value = "Authorization", required = false) String token,
                                                    @PathVariable String text) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse != null) {
            return tokenCheckResponse;
        }
        return ResponseEntity.ok(Map.of("certSchedule", certificateService.getCertificationSchedule(text)));
    }

    @GetMapping("/search/certificate")
    @ResponseBody
    public ResponseEntity<?> getSearchCertificate(@RequestHeader(value = "Authorization", required = false) String token,
                                                  @RequestParam String text
                                         , HttpServletRequest request) {

        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse != null) {
            return tokenCheckResponse;
        }
        return ResponseEntity.ok(Map.of("certificate", certificateService.getCertificate(text, request.getRemoteAddr())));
    }
    @GetMapping("/certificate/category/{mainCategory}/{subCategory}")
    @ResponseBody
    public ResponseEntity<?> getMainCategoryAndSubCategory(@RequestHeader(value = "Authorization", required = false) String token,
                                                    @PathVariable String mainCategory ,@PathVariable String subCategory ) {
        ResponseEntity<?> tokenCheckResponse = tokenValidator.validateTokenOrRespond(token);
        if (tokenCheckResponse != null) {
            return tokenCheckResponse;
        }
        return ResponseEntity.ok(Map.of("certSchedule",certificateService.getMainCategoryAndSubCategory(mainCategory,subCategory)));
    }
}
