package es.board.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.req.JobListing;
import es.board.controller.model.req.StudyTipRequest;
import es.board.controller.model.req.WantedJobData;
import es.board.repository.CertificateDAO;
import es.board.repository.entity.entityrepository.TistoryPostRepository;
import es.board.service.ItCrawlingService;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class ItCrawlingServiceImpl implements ItCrawlingService {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final AsyncService asyncService;

    private  final TistoryPostRepository tistoryPostRepository;

    private final CertificateDAO certificateDAO;
    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 100;


    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";
    private static final String JUMPIT_URL = "https://jumpit-api.saramin.co.kr/api/positions?jobCategory=1&jobCategory=2&jobCategory=3&jobCategory=9&career=0&sort=rsp_rate&highlight=false";

    private  static  final String GMARKET_URL="https://m.gmarket.co.kr/n/superdeal?categoryCode=400000140";

    private static final String PROGRAMMERS_URL = "https://career.programmers.co.kr/api/job_positions?min_career=0&order=recent&page=1&job_category_ids[]=1&job_category_ids[]=25&tags[]=Java&tags[]=Spring";

    private static final String JOBPLANET_URL = "https://www.jobplanet.co.kr/job";


    private static final String TISTORY_SEARCH_URL = "https://www.tistory.com/search?keyword=";

    private static final String NAVER_SEARCH_URL = "https://m.search.naver.com/search.naver?ssc=tab.m_blog.all&sm=tab_jum&query=";

    private static final String WANTED_URL = "https://www.wanted.co.kr/wdlist/518?country=kr&job_sort=job.latest_order&years=0&selected=872&selected=873&selected=10110&selected=660&selected=669&locations=seoul.all&locations=incheon.all&locations=gyeonggi.all";

    @Override
    public List<Map<String, Object>> jumPitList() {
        List<Map<String, Object>> jobList = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 2. API 요청 실행
            ResponseEntity<String> responseEntity = restTemplate.exchange(JUMPIT_URL, HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody();

            // 3. JSON 파싱
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode positions = rootNode.path("result").path("positions");
//            JsonNode positions = rootNode.path("positions");
            // 4. 데이터 가공
            for (JsonNode position : positions) {
                Map<String, Object> jobInfo = new HashMap<>();
                jobInfo.put("id", position.get("id"));
                jobInfo.put("jobCategory", position.get("jobCategory"));
                jobInfo.put("companyName", position.get("companyName"));
                jobInfo.put("title", position.get("title"));
                jobInfo.put("techStacks", position.get("techStacks"));
                jobInfo.put("locations", position.get("locations"));
                jobInfo.put("applyAvailable", !position.get("alwaysOpen").asBoolean());

                jobList.add(jobInfo);
            }
            log.info(jobList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobList;
    }
    @Override
    public List<Map<String, Object>> gmarketList() {
        List<Map<String, Object>> deals = new ArrayList<>();
        try {
            Connection connection = Jsoup.connect("https://www.gmarket.co.kr/n/superdeal?jaehuid=200011415")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com/")
                    .header("Accept-Language", "ko-KR,ko;q=0.9")
                    .timeout(10000)
                    .method(Connection.Method.GET);

            Document doc = connection.get();

            Elements items = doc.select(".box__itemcard-superdeal--inner");

            for (Element item : items) {
                String title = item.selectFirst(".text__title") != null
                        ? item.selectFirst(".text__title").text()
                        : "제목 없음";

                String price = item.selectFirst(".text__price") != null
                        ? item.selectFirst(".text__price").text()
                        : "가격 정보 없음";

                String imgUrl = item.selectFirst(".box-product__img-product img") != null
                        ? item.selectFirst(".box-product__img-product img").attr("src")
                        : "";

                Element linkElement = item.selectFirst("a.box__itemcard-superdeal--link");
                String dealLink = linkElement != null ? linkElement.absUrl("href") : "";

                Map<String, Object> deal = new HashMap<>();
                deal.put("title", title);
                deal.put("price", price);
                deal.put("imgUrl", imgUrl.startsWith("http") ? imgUrl : "https:" + imgUrl);
                deal.put("link", dealLink);

                deals.add(deal);
            }
        } catch (Exception e) {
            log.error("G마켓 특가 크롤링 오류", e);
        }
        return deals;
    }


    @Override
    public List<Map<String, Object>> programmersList() {
        List<Map<String, Object>> jobList = new ArrayList<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "application/json");
//            headers.set("Referer", "https://career.programmers.co.kr");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 📝 API 요청 실행
            ResponseEntity<String> responseEntity = restTemplate.exchange(PROGRAMMERS_URL, HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody();

            // 🌟 JSON 파싱
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode positions = rootNode.path("jobPositions");

            // ✅ 데이터 가공
            for (JsonNode position : positions) {
                Map<String, Object> jobInfo = new HashMap<>();

                jobInfo.put("id", position.get("id").asInt());
                jobInfo.put("title", position.get("title").asText());
                jobInfo.put("companyName", position.path("company").path("name").asText());
                jobInfo.put("companyId", position.path("company").path("id").asInt());
                jobInfo.put("address", position.path("company").path("address").asText());
                jobInfo.put("careerMax", position.path("max_career").asInt());
                jobInfo.put("jobType", position.path("employment_type").asText());
                jobInfo.put("applyAvailable", position.path("status").asText().equals("active"));
                jobInfo.put("url", "https://career.programmers.co.kr/job_positions/" + position.get("id").asText());

                List<String> techStackList = new ArrayList<>();
                for (JsonNode techTag : position.path("technicalTags")) {
                    techStackList.add(techTag.path("name").asText());
                }
                jobInfo.put("technicalTags", techStackList);

                jobList.add(jobInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobList;
    }

    @Override
    public List<JobListing> jobPlanetList() {
        List<JobListing> jobList = new ArrayList<>();


        WebDriverManager.chromedriver().setup();


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.get(JOBPLANET_URL);


        List<WebElement> jobElements = driver.findElements(By.cssSelector(".overflow-hidden.medium"));

        for (WebElement jobElement : jobElements) {
            try {
                String jobTitle = jobElement.findElement(By.cssSelector(".jf_gray800.jf_h9.line-clamp-2")).getText();
                String companyName = jobElement.findElement(By.cssSelector(".jf_b2.jf_gray500")).getText();
                String jobUrl = jobElement.findElement(By.tagName("a")).getAttribute("href");

                jobList.add(new JobListing(jobTitle, companyName, jobUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        driver.quit();

        return jobList;
    }



    @Override
    public List<WantedJobData> wantedList() {
        List<WantedJobData> jobList = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(WANTED_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36")
                    .timeout(5000)
                    .get();
            Elements jobCards = doc.select("div.JobList_JobList__contentWrapper__3wwft");
            for (Element jobElement : jobCards) {
                Element jobLink = jobElement.selectFirst("a[data-attribute-id=position__click]");
                if (jobLink != null) {
                    String positionName = jobLink.attr("data-position-name");
                    String companyName = jobLink.attr("data-company-name");
                    String jobUrl = "https://example.com" + jobLink.attr("href"); // 도메인 추가 필요
                    Element imgElement = jobElement.selectFirst("img");
                    String imgUrl = imgElement != null ? imgElement.attr("src") : "";
                    Element locationElement = jobElement.selectFirst(".CompanyNameWithLocationPeriod_CompanyNameWithLocationPeriod__location__FHNmN");
                    String location = locationElement != null ? locationElement.text() : "";

                    jobList.add(new WantedJobData(positionName, companyName, jobUrl, imgUrl, location));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jobList;
    }

    @Override
    public List<StudyTipRequest> crawlNaverStudyTips(String keyword) {
        List<StudyTipRequest> studyTips = new ArrayList<>();
        String searchUrl = NAVER_SEARCH_URL + keyword.replace(" ", "+");

        try {
            Document doc = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .get();

            Elements blogItems = doc.select(".detail_box"); // 검색 결과 블로그 항목

            for (Element item : blogItems) {
                Element titleElement = item.selectFirst(".title_area a.title_link");
                Element imageElement = item.selectFirst(".mod_ugc_thumb_area img");
                Element descElement = item.selectFirst(".dsc_area a.dsc_link");

                if (titleElement != null) {
                    String title = titleElement.text().trim();
                    String link = titleElement.attr("href");
                    String image = imageElement != null ? imageElement.attr("src") : null;
                    String description = descElement != null ? descElement.text().trim() : "";

                    studyTips.add(new StudyTipRequest(title, link, image, description));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return studyTips;
    }

    @Override
    public List<StudyTipRequest> crawlGoogleStudyTips(String keyword) {
        List<StudyTipRequest> studyTips = new ArrayList<>();
        String searchUrl = GOOGLE_SEARCH_URL + keyword;

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(searchUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.g")));

            List<WebElement> searchResults = driver.findElements(By.cssSelector("div.g"));

            for (WebElement result : searchResults) {
                try {
                    WebElement titleElement = result.findElement(By.cssSelector("h3"));
                    String title = titleElement.getText().trim();

                    WebElement linkElement = result.findElement(By.cssSelector("a"));
                    String link = linkElement.getAttribute("href");

                    WebElement descElement = result.findElement(By.cssSelector("div.VwiC3b"));
                    String description = (descElement != null) ? descElement.getText().trim() : "";

                    studyTips.add(new StudyTipRequest(title, link, null, null));
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return studyTips;
    }

    @Override
    public  void crawlTistoryPosts(String keywords) {
        List<String> certificationNames = certificateDAO.getCertificationNames();
        List<es.board.repository.entity.TistoryPost> postList = new ArrayList<>();
        Map<String, Integer> keywordCount = new HashMap<>();
        for (String keyword : certificationNames) {
            if (keywordCount.getOrDefault(keyword, 0) >= 10) {
                log.warn("'{}' 키워드 크롤링 제한 (최대 10개) - 스킵", keyword);
                continue;
            }
            try {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
                WebDriver driver = new ChromeDriver(options);
                driver.get(TISTORY_SEARCH_URL + keyword.replace(" ", "%20"));
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.item_group")));
                List<WebElement> items = driver.findElements(By.cssSelector("div.item_group"));

                for (WebElement item : items) {
                    try {

                        String blogName = item.findElement(By.cssSelector("div.wrap_profile a.link_blog span.txt_g")).getText();
                        String blogUrl = item.findElement(By.cssSelector("div.wrap_profile a.link_blog")).getAttribute("href");
                        String title = item.findElement(By.cssSelector("a.link_cont div.wrap_tit strong.tit_cont")).getText();
                        String postUrl = item.findElement(By.cssSelector("a.link_cont")).getAttribute("href");
//                        String description = item.findElement(By.cssSelector("a.link_cont div.wrap_tit div.wrap_desc p.desc_g")).getText();
                        String thumbnailUrl = item.findElement(By.cssSelector("a.link_cont div.wrap_thumb img[alt='글 섬네일']")).getAttribute("src");
                        postList.add(es.board.repository.entity.TistoryPost.builder()
                                .name(keyword)
                                .blogName(blogName)
                                .blogUrl(blogUrl)
                                .title(title)
                                .postUrl(postUrl)
                                .thumbnailUrl(thumbnailUrl)
                                .build());
                        if (postList.size() >= BATCH_SIZE) {
                            log.info(String.valueOf(postList.size()));
                            savePostsInBatch(postList);
                            postList.clear();
                        }
                    } catch (Exception e) {
                        log.warn("⚠일부 요소를 찾을 수 없음: {}", e.getMessage());
                    }
                }
                driver.quit();
                if (!postList.isEmpty()) {
                    savePostsInBatch(postList);
                }
                Thread.sleep(10000);
            } catch (Exception e) {
                log.error("크롤링 중 오류 발생: ", e);
            }
        }
        log.info("비동기 크롤링 완료!");
    }

//    public List<TistoryPost> crawlTistoryPostEx(String keyword) {
//        List<TistoryPost> postList = new ArrayList<>();
//        String searchUrl = "https://tistory.com/search?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
//
//        try {
//            Document doc = Jsoup.connect(searchUrl)
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
//                    .timeout(15000)  // 타임아웃 증가
//                    .get();
//
//            Elements items = doc.select("list_tistory_top");
//
//            log.info(items.toString());
//            for (Element item : items) {
//                try {
//                    String blogName = item.select("div.wrap_profile a.link_blog span.txt_g").text();
//                    String blogUrl = item.select("div.wrap_profile a.link_blog").attr("href");
//                    String title = item.select("a.link_cont div.wrap_tit strong.tit_cont").text();
//                    String postUrl = item.select("a.link_cont").attr("href");
//                    String description = item.select("a.link_cont div.wrap_tit div.wrap_desc p.desc_g").text();
//                    String date = item.select("a.link_cont div.wrap_tit div.wrap_info span.txt_g:last-child").text();
//                    String thumbnailUrl = item.select("a.link_cont div.wrap_thumb img").attr("src");
//
//                    postList.add(new TistoryPost(blogName, blogUrl, title, postUrl, description, date, thumbnailUrl));
//                } catch (Exception e) {
//                    continue;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return postList;
//    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePostsInBatch(List<es.board.repository.entity.TistoryPost> posts) {
        int batchSize = 20;

        for (int i = 0; i < posts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, posts.size());
            List<es.board.repository.entity.TistoryPost> batch = posts.subList(i, end);
            tistoryPostRepository.bulkInsertBatch(batch);
        }

        entityManager.flush();
        entityManager.clear();
    }

}


