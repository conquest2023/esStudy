package es.board.controller.feed;

import es.board.controller.model.dto.PostDetailResponse;
import es.board.domain.feed.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Slf4j
@Controller
public class PostHtmlController {

    @Autowired
    private PostService postService;

    @GetMapping("/post/{id}")
    public String getPostForRobot(@PathVariable int id,
                                  @RequestHeader(value = "User-Agent", required = false) String userAgent,
                                  Model model) {


        String ua = userAgent.toLowerCase();
        boolean isRobot = ua.contains("kakaotalk") ||
                ua.contains("facebookexternalhit") ||
                ua.contains("twitterbot") ||
                ua.contains("googlebot") ||
                ua.contains("bingbot");
        log.info("컨트롤러 진입 성공!");
        if (isRobot) {
            PostDetailResponse post = postService.findPostDetail(null, id);
            model.addAttribute("post", post);
            return "robot-meta";
        }

        return "redirect:/?postId=" + id;
    }
}