package es.board.service.impl;


import es.board.controller.model.res.FeedCreateResponse;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.UserDAO;
import es.board.repository.entity.Post;
import es.board.repository.entity.User;
import es.board.repository.entity.entityrepository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncService {

    private  final PostRepository postRepository;

    private  final UserDAO userDAO;


    @Async("taskExecutor")
    public void savePostAsync(FeedCreateResponse feedSaveDTO) {
        log.info("비동기 작업 시작 - 스레드: {}", Thread.currentThread().getName());
        Post post = new Post();
        Post savedPost = postRepository.save(post.PostToEntity(feedSaveDTO));
        log.info("MySQL 저장 완료 - ID: {}, 스레드: {}", savedPost.getId(), Thread.currentThread().getName());
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
    public void saveUserAsync(SignUpResponse sign,String password) {
        User user=new User();
        log.info("비동기 아이디 생성 작업 시작 - 스레드: {}", Thread.currentThread().getName());
        userDAO.createUser(user.DtoToUser(sign,password));
        log.info("MySQL 아이디 생성 완료 - ID: {}, 스레드: {}",sign, Thread.currentThread().getName());
    }
}
