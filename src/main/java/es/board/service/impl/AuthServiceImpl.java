package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.CommentMapper;
import es.board.controller.model.dto.feed.FeedDTO;
import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
import es.board.ex.TokenInvalidException;
import es.board.repository.UserDAO;
import es.board.repository.document.Comment;
import es.board.repository.entity.PointHistory;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.repository.entity.repository.UserRepository;
import es.board.repository.entity.User;
import es.board.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final JwtTokenProvider jwtTokenProvider;


    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserRepository userRepository;

    private final CommentMapper commentMapper;

    private final AsyncService asyncService;

    private final PointHistoryRepository pointHistoryRepository;

    private final UserDAO userDAO;

    private final StringRedisTemplate redisTemplate;



    private final PasswordEncoder passwordEncoder;


    @Override
    public void createUser(SignUpDTO sign) {
        User user = new User();
        String password = passwordEncoder.encode(sign.getPassword());
        asyncService.saveUserAsync(sign, password);
        userRepository.save(user.DtoToUser(sign, password));

    }

    @Override
    public void updateVisitCount(String userId) {
        userDAO.updateVisitCount(userId);
    }

    @Override
    @Transactional
    public boolean login(LoginDTO login) {
        userRepository.updateLastLogin(login.getUserId(), LocalDateTime.now());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login.getUserId(), login.getPassword());
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        grantLoginPoint(login.getUserId());
        return true;
    }

    @Override
    public String findById(String  username){
        Optional<String> userId=userRepository.findById(username);
        if (userId.isPresent()){
            return  userId.get();
        }else {
            throw  new RuntimeException("찾으시는 유저가 없습니다");
        }
    }

    @Override
    public Boolean checkId(SignUpDTO sign) {
        if (userRepository.findByUserid(sign.getUserId()) == null) {
            return true;
        }
        return false;
    }


    @Override
    public List<CommentDTO.Request> getCommentOwnerList(Object comments, String commentOwner, String feedUID, String userId) {
        if (!(comments instanceof List<?>)) {
            throw new IllegalArgumentException("comments 파라미터가 List<CommentRequest> 타입이 아닙니다.");
        }
        List<CommentDTO.Request> commentList = commentMapper.changeCommentListDTO((List<Comment>) comments);
        return commentList.stream()
                .peek(comment -> {
                    comment.setAuthor(comment.getUserId().equals(userId));
                    comment.setCommentOwner(comment.getUserId().equals(commentOwner));
                })
                .collect(Collectors.toList());
    }

    @Override
    public Boolean extractUserIdFromToken(String token, FeedDTO.Response response) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new TokenInvalidException("토큰이 비어있습니다.");
        }
        token = token.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            throw  new TokenInvalidException("권한이 없습니다");
        }
        response.setUserId(jwtTokenProvider.getUserId(token));
        return true;
    }

    @Override
    public void autoLogin(String userId,String token) {

        redisTemplate.opsForValue().set("RT:" + userId,
                token, Duration.ofDays(7) // 만료시간 설정
        );
    }

    @Override
    public Long findVisitCount(String userId) {
        return userDAO.findVisitCount(userId);
    }

    @Override
    public Authentication authenticate(LoginDTO login) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login.getUserId(), login.getPassword());
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    public void grantLoginPoint(String userId) {
        String today = LocalDate.now().toString();
        String key = "login:point:" + userId + ":" + today;
        if (redisTemplate.hasKey(key)) {
            log.info("이미 로그인 포인트를 받은 유저입니다: {}", userId);
            return;
        }
        createPointHistory(userId);
        redisTemplate.opsForValue().set(key, "done", Duration.ofDays(1));
        log.info("로그인 포인트 지급 완료: {}", userId);
    }

    public void createPointHistory(String userId) {
        PointHistory history = PointHistory.builder()
                .userId(userId)
                .pointChange(2)
                .reason("로그인")
                .createdAt(LocalDateTime.now())
                .build();
        pointHistoryRepository.save(history);
    }
}