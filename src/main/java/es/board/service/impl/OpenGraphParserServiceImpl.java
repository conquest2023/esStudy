package es.board.service.impl;

import es.board.service.OpenGraphParserService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class OpenGraphParserServiceImpl implements OpenGraphParserService {

    @Override
    public Map<String, String> extractOGMeta(String url) {
        Map<String, String> ogData = new HashMap<>();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla") // 크롤러 차단 우회
                    .timeout(5000)
                    .get();

            String title = doc.select("meta[property=og:title]").attr("content");
            String image = doc.select("meta[property=og:image]").attr("content");
            String desc  = doc.select("meta[property=og:description]").attr("content");

            ogData.put("title", title != null ? title : "No Title");
            ogData.put("image", image != null ? image : "/img/default-og.jpg");
            ogData.put("description", desc != null ? desc : "설명이 없습니다");

        } catch (Exception e) {
            ogData.put("title", "파싱 실패");
            ogData.put("image", "/img/default-og.jpg");
            ogData.put("description", "미리보기를 불러올 수 없습니다");
        }
        return  ogData;
    }
}
