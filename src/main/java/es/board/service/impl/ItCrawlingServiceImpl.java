package es.board.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.req.JobListing;
import es.board.controller.model.req.WantedJobData;
import es.board.service.ItCrawlingService;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class ItCrawlingServiceImpl implements ItCrawlingService {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private static final String JUMPIT_URL = "https://jumpit-api.saramin.co.kr/api/positions?jobCategory=1&jobCategory=2&jobCategory=3&jobCategory=9&career=0&sort=rsp_rate&highlight=false";


    private static final String PROGRAMMERS_URL = "https://career.programmers.co.kr/api/job_positions?min_career=0&order=recent&page=1&job_category_ids[]=1&job_category_ids[]=25&tags[]=Java&tags[]=Spring";

    private static final String JOBPLANET_URL = "https://www.jobplanet.co.kr/job";

    private static final String WANTED_URL = "https://www.wanted.co.kr/wdlist/518?country=kr&job_sort=job.latest_order&years=0&selected=872&selected=873&selected=10110&selected=660&selected=669&locations=seoul.all&locations=incheon.all&locations=gyeonggi.all";

    @Override
    public List<Map<String, Object>> jumPitList() {
        List<Map<String, Object>> jobList = new ArrayList<>();

        try {
            // 1. HTTP 요청 헤더 설정 (User-Agent 추가)
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 2. API 요청 실행
            ResponseEntity<String> responseEntity = restTemplate.exchange(JUMPIT_URL, HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody();

            // 응답 확인 (디버깅용)


            // 3. JSON 파싱
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode positions = rootNode.path("result").path("positions");
//            JsonNode positions = rootNode.path("positions");
            // 4. 데이터 가공
            for (JsonNode position : positions) {
                Map<String, Object> jobInfo = new HashMap<>();
                jobInfo.put("id", position.get("id"));
                jobInfo.put("jobCategory",position.get("jobCategory"));
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
    public List<Map<String, Object>> programmersList() {
        List<Map<String, Object>> jobList = new ArrayList<>();
        try {
            // 🌟 HTTP 요청 헤더 설정
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
//                jobInfo.put("careerMin", position.path("min_career").asInt());
                jobInfo.put("careerMax", position.path("max_career").asInt());
//                jobInfo.put("salary", position.path("salary").asText(null)); // 연봉 정보
                jobInfo.put("jobType", position.path("employment_type").asText()); // 정규직, 계약직 등
                jobInfo.put("applyAvailable", position.path("status").asText().equals("active")); // 지원 가능 여부
//                jobInfo.put("createdAt", position.path("created_at").asText());
//                jobInfo.put("updatedAt", position.path("updated_at").asText());
                jobInfo.put("url", "https://career.programmers.co.kr/job_positions/" + position.get("id").asText()); // 채용 공고 URL

                // ✅ 기술 스택 추출
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

        // 1. ChromeDriver 자동 설정
        WebDriverManager.chromedriver().setup();

        // 2. Chrome 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // (테스트 후 필요시 주석)
        options.addArguments("--disable-blink-features=AutomationControlled");  // Bot 탐지 우회
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");

        // 3. WebDriver 실행
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.get(JOBPLANET_URL);

        // 4. 채용 공고 목록 탐색
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

        // 5. WebDriver 종료
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
            Elements jobCards= doc.select("div.JobList_JobList__contentWrapper__3wwft");
            log.info(doc.data());
            for (Element jobElement : jobCards) {
                Element jobLink = jobElement.selectFirst("a[data-attribute-id=position__click]");
                if (jobLink != null) {
                    String positionName = jobLink.attr("data-position-name");
                    String companyName = jobLink.attr("data-company-name");
                    String jobUrl = "https://example.com" + jobLink.attr("href"); // 도메인 추가 필요

                    // 이미지 가져오기
                    Element imgElement = jobElement.selectFirst("img");
                    String imgUrl = imgElement != null ? imgElement.attr("src") : "";

                    // 위치 정보 가져오기
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
}



