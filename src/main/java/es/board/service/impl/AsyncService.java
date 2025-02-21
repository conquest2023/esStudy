package es.board.service.impl;


import es.board.controller.model.req.ScheduleDTO;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.FeedDAO;
import es.board.repository.LikeDAO;
import es.board.repository.ScheduleDAO;
import es.board.repository.UserDAO;
import es.board.repository.document.Schedule;
import es.board.repository.entity.Post;
import es.board.repository.entity.User;
import es.board.repository.entity.entityrepository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncService {

    private  final PostRepository postRepository;

    private  final UserDAO userDAO;

    private  final LikeDAO likeDAO;

    private  final FeedDAO feedDAO;

    private  final ScheduleDAO scheduleDAO;
    @Async("taskExecutor")
    public CompletableFuture<Void> savePostAsync(FeedCreateResponse feedSaveDTO) {
        log.info("비동기 Elasticsearch 저장 시작 - 스레드: {}", Thread.currentThread().getName());

        try {
            feedDAO.indexSaveFeed(feedSaveDTO); // Elasticsearch 저장
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
    @Transactional
    public void deletePostAsync(String userId) {
        log.info("비동기 삭제 작업 시작 - 스레드: {}", Thread.currentThread().getName());
        postRepository.deleteById(userId);
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
}
