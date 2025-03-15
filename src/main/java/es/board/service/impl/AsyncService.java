package es.board.service.impl;


import es.board.controller.model.req.NoticeDTO;
import es.board.controller.model.req.VoteResponse;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.*;
import es.board.repository.document.Board;
import es.board.repository.document.Schedule;
import es.board.repository.entity.TistoryPost;
import es.board.repository.entity.User;
import es.board.repository.entity.entityrepository.PostRepository;
import es.board.repository.entity.entityrepository.TistoryPostRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncService {

    private final PostRepository postRepository;

    private final UserDAO userDAO;

    private final LikeDAO likeDAO;

    private final FeedDAO feedDAO;

    private  final  TistoryPostRepository tistoryPostRepository;

    private final CertificateDAO certificateDAO;

    private  final  VoteDAO voteDAO;

    private static final String TISTORY_SEARCH_URL = "https://www.tistory.com/search?keyword=";

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 100;

    private final ScheduleDAO scheduleDAO;

    @Async("taskExecutor")
    public CompletableFuture<List<TistoryPost>> crawlTistoryPostsAsync() {
        List<String> certificationNames = certificateDAO.getCertificationNames();
        List<TistoryPost> postList = new ArrayList<>();

        for (String keyword : certificationNames) {
            try {
                log.info("비동기 크롤링 시작 - 키워드: {}", keyword);

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
                        log.info(blogUrl);
                        postList.add(TistoryPost.builder()
                                .name(keyword)
                                .blogName(blogName)
                                .blogUrl(blogUrl)
                                .title(title)
                                .postUrl(postUrl)
                                .thumbnailUrl(thumbnailUrl)
                                .build());
                        if (postList.size() >= BATCH_SIZE) {
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

                log.info("'{}' 키워드 크롤링 완료 ({}개 게시글)", keyword, postList.size());


                Thread.sleep(10000);

            } catch (Exception e) {
                log.error("크롤링 중 오류 발생: ", e);
            }
        }
        log.info("비동기 크롤링 완료!");
        return CompletableFuture.completedFuture(postList);
    }

    @Transactional
    public void savePostsInBatch(List<TistoryPost> posts) {
        for (TistoryPost post : posts) {
            tistoryPostRepository.bulkInsert(
                    post.getName(),
                    post.getBlogName(),
                    post.getBlogUrl(),
                    post.getTitle(),
                    post.getPostUrl(),
                    post.getThumbnailUrl()
            );
        }
        entityManager.flush();
        entityManager.clear();
    }


    @Async("taskExecutor")
    public CompletableFuture<Void> saveVoteAsync(VoteResponse vote, Long id) {
        log.info("비동기 Vote Elasticsearch 저장 시작 - 스레드: {}", Thread.currentThread().getName());

        try {
            voteDAO.saveVoteContent(vote,id);
            log.info("비동기 Vote Elasticsearch 저장 완료 - 스레드: {}", Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> saveAggregationVoteAsync(VoteResponse vote, Long id) {
        log.info("비동기 Vote Elasticsearch 저장 집계 시작 - 스레드: {}", Thread.currentThread().getName());

        try {
            voteDAO.saveAggregationAgreeVote(vote,id);
            log.info("비동기 Vote Elasticsearch 집계 저장 완료 - 스레드: {}", Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패", e);
        }
        return CompletableFuture.completedFuture(null);
    }


    @Async("taskExecutor")
    public CompletableFuture<Void> savePostAsync(Board board, int postId) {
        log.info("비동기 Elasticsearch 저장 시작 - 스레드: {}", Thread.currentThread().getName());
        try {
            feedDAO.indexSaveFeed(board,postId);
            log.info("비동기 Elasticsearch 저장 완료 - 스레드: {}", Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> saveScheduleAsync(Schedule scheduleDTO) {
        log.info("비동기 Elasticsearch 저장 시작 - 스레드: {}", Thread.currentThread().getName());
        try {
            scheduleDAO.saveSchedule(scheduleDTO);
            log.info("비동기 Elasticsearch 저장 완료 - 스레드: {}", Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패", e);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> saveScheduleBulkAsync(List<Schedule> schedule) {
        log.info("비동기 Elasticsearch 저장 시작 - 스레드: {}", Thread.currentThread().getName());
        try {
            scheduleDAO.saveScheduleBulk(schedule);
            log.info("비동기 Elasticsearch 저장 완료 - 스레드: {}", Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패", e);
        }

        return CompletableFuture.completedFuture(null);
    }



    @Async("taskExecutor")
    @Transactional
    public void deletePostAsync(String id,String userId) {
        log.info("비동기 삭제 작업 시작 - 스레드: {}", Thread.currentThread().getName());
        postRepository.deleteById(id,userId);
        log.info("MySQL 삭제 완료 - ID: {}, 스레드: {}",userId, Thread.currentThread().getName());
    }

    @Async("taskExecutor")
    @Transactional
    public void cancelLike(String userId,String  feedId) {

        likeDAO.cancelLike(feedId);
        log.info("좋아요 삭제 완료 - ID: {}, 스레드: {}",userId, Thread.currentThread().getName());
    }

    @Async("taskExecutor")
    @Transactional
    public void  postLike(String userId,String feedId) {
        likeDAO.saveLike(feedId);
        log.info("좋아요 저장 완료 - ID: {}, 스레드: {}",userId, Thread.currentThread().getName());
    }


    @Async("taskExecutor")
    @Transactional
    public void saveUserAsync(SignUpResponse sign,String password) {
        User user=new User();
        log.info("비동기 아이디 생성 작업 시작 - 스레드: {}", Thread.currentThread().getName());
        userDAO.createUser(user.DtoToUser(sign,password));
        log.info("MySQL 아이디 생성 완료 - ID: {}, 스레드: {}",sign, Thread.currentThread().getName());
    }


    @Async("taskExecutor")
    public CompletableFuture<Void> saveNoticeAsync(NoticeDTO noticeDTO,Long id) {
        log.info("비동기 Elasticsearch 저장 시작 - 스레드: {}", Thread.currentThread().getName());
            feedDAO.saveNoticeFeed(noticeDTO,id);
        try {
            log.info("비동기 Elasticsearch 저장 완료 - 스레드: {}", Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패", e);
        }

        return CompletableFuture.completedFuture(null);
    }
}
