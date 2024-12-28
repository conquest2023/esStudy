package es.board.controller;

import es.board.service.CommentService;
import es.board.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    private  final CommentService commentService;


    @GetMapping("/login")
    public String login() {
        log.info("helldassda");
        return "basic/feed/login";
    }

    @PostMapping("/login/pass")
    public String  loginPass(Model model) {
        feedMain(model);
        log.info("helldassdasdasa");
        return "basic/feed/feedList";
    }


    private void feedMain(Model model){
        int maxPage = (int) Math.ceil((double) feedService.getTotalPage(page,size) / size);
        int totalPage=(int) Math.ceil( feedService.getTotalFeed());
        model.addAttribute("viewCount",feedService.getViewCountAll());
        model.addAttribute("count",commentService.getPagingComment(feedService.getfeedUIDList(page,size),page,size));
        model.addAttribute("page", page);  // 현재 페이지 번호
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("data", feedService.getPagingFeed(page, size)); // 서비스 호출 시 페이지와 크기 전달
        model.addAttribute("month",feedService.getMonthPopularFeed());
    }
}
