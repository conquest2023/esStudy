package es.board.controller.feed;

import es.board.controller.model.dto.PostDetailResponse;
import es.board.domain.feed.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class PostHtmlController {

    @Autowired
    private PostService postService;

    @GetMapping("/post/{id}")
    public String getPostForRobot(@PathVariable int id,
                                  @RequestHeader(value = "User-Agent", required = false) String userAgent,
                                  Model model) {

        // 1. 로봇인지 확인
        boolean isRobot = userAgent != null && userAgent.matches(".*(Kakaotalk-Scr|facebookexternalhit|Twitterbot).*");

        if (isRobot) {
            // 로봇용 데이터 조회 (간단하게 제목/이미지만)
            PostDetailResponse post = postService.findPostDetail(null, id);
            model.addAttribute("post", post);

            return "robot-meta";
        }

        return "forward:/index.html";
    }
}