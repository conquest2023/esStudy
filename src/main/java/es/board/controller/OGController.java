package es.board.controller;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.service.FeedService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class OGController {

    private  final FeedService feedService;

    @GetMapping("/search/view/feed/id/{feedUID}")
    public Object handleOGBotRedirect(@PathVariable String feedUID, HttpServletRequest request) {
        if (isSocialMediaBot(request)) {
            log.info("봇 접근 감지됨! feedUID = {}", feedUID);
            return "redirect:/search/view/og/feed/id?id=" + feedUID;
        }
        log.info("forward:/index.html");
        return "forward:/index.html";
    }


    @GetMapping("/search/view/og/feed/id")
    public String serveOGPage(@RequestParam("id") String id, Model model) {

        log.info("sadads");
        PostDTO.Request feed = feedService.getFeedDetail(id);
        model.addAttribute("title", feed.getTitle());
        model.addAttribute("description", feed.getDescription());
//        model.addAttribute("image", feed.getImageURL());
        model.addAttribute("url", "https://workly.info/search/view/feed/id/" + id);

        return "/basic/feed/FeedDetails"; // ✅ OG 태그 포함된 Thymeleaf 템플릿
    }

    private boolean isSocialMediaBot(HttpServletRequest request) {

        log.info("로직 발동");
        log.info(request.getHeader("User-Agent"));
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return false;

        userAgent = userAgent.toLowerCase();

        return userAgent.contains("kakaotalk") ||
                userAgent.contains("facebookexternalhit") ||
                userAgent.contains("twitterbot") ||
                userAgent.contains("slackbot") ||
                userAgent.contains("discordbot") ||
                userAgent.contains("whatsapp") ||
                userAgent.contains("telegrambot");
    }
}
