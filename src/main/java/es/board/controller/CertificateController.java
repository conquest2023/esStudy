package es.board.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@Controller
public class CertificateController {



    @GetMapping("/certificate/list")
    public String certificateList(){
        return  "basic/feed/Certificate";
    }
}
