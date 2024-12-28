package es.board.controller;

import es.board.model.res.LoginResponse;
import es.board.model.res.SignUpResponse;
import es.board.service.CommentService;
import es.board.service.FeedService;
import es.board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private  final int page=0;

    private  final int size=10;

    private  final FeedService feedService;

    private  final CommentService commentService;

    private  final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "basic/login/Login";
    }

    @GetMapping("/signup")
    public String signUp() {

        return "basic/login/SignUp";
    }
    @PostMapping("/signup/pass")
    public  String signIn(@ModelAttribute SignUpResponse sign, Model model){
        boolean isIdAvailable = userService.checkId(sign);
        log.info("아이디 중복 여부: " + isIdAvailable);

        if (isIdAvailable) {
            userService.createUser(sign);
            return "redirect:/login"; // 아이디가 사용 가능하면 로그인 페이지로 리다이렉트
        } else {
            model.addAttribute("errorButton", false); // 중복된 경우 false 전달
            model.addAttribute("error", "아이디가 중복됩니다.");
            model.addAttribute("signUpResponse", sign);
            return "basic/login/SignUp"; // 다시 회원가입 페이지로 이동
        }
    }
    @PostMapping("/check")
    public ResponseEntity<Boolean> checkUserId(@RequestBody SignUpResponse sign) {
        boolean isAvailable = userService.checkId(sign);
        log.info(String.valueOf(isAvailable));
        return ResponseEntity.ok(isAvailable); // true: 사용 가능, false: 중복
    }


    @PostMapping("/login/pass")
    public String  loginPass(Model model,  LoginResponse response) {

        if (!userService.login(response)){
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "basic/login/Login";
        }else{
            feedMain(model);
            return "basic/feed/feedList";
        }

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
