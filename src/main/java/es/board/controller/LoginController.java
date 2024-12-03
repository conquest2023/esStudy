package es.board.controller;

import es.board.model.res.LoginResponse;
import es.board.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private  final int page=0;

    private  final int size=10;

    private  final FeedService feedService;

    @PostMapping("/login")
    public String login(Model model) throws IOException {

        feedMain(model);
        log.info("helldassda");
        return "/basic/feed/feedList";
    }




    private void feedMain(Model model) throws IOException {
        int maxPage = (int) Math.ceil((double) feedService.getTotalPage(page,size) / size);
        model.addAttribute("page",page);  // 현재 페이지 번호
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("data", feedService.getPagingFeed(page, size)); // 서비스 호출 시 페이지와 크기 전달
        model.addAttribute("month",feedService.getMonthPopularFeed());
    }
}
