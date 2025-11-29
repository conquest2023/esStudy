package es.board.service.impl;

import es.board.controller.model.dto.feed.*;
import es.board.domain.point.PointService;
import es.board.repository.entity.PointHistoryEntity;
import es.board.repository.entity.repository.PointHistoryRepository;
import es.board.repository.entity.repository.UserRepository;
import es.board.infrastructure.entity.user.User;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PointService pointService;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserRepository userRepository;

    private final StringRedisTemplate redisTemplate;

    private final PasswordEncoder passwordEncoder;


    @Override
    public void registerUser(SignUpDTO sign) {
        User user = new User();
        String password = passwordEncoder.encode(sign.getPassword());
        userRepository.save(user.DtoToUser(sign, password));

    }

    @Override
    @Transactional
    public boolean login(LoginDTO login) {
        userRepository.updateLastLogin(login.getUserId(), LocalDateTime.now());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login.getUserId(), login.getPassword());
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        pointService.grantActivityPoint(login.getUserId(),"로그인",3,1);
        return true;
    }

    @Override
    public User findByUser(String  username){
        Optional<User> user=userRepository.findByUser(username);
        if (user.isPresent()){
            return  user.get();
        }else {
            throw  new RuntimeException("찾으시는 유저가 없습니다");
        }
    }

    @Override
    public void updateLastLogin(String userId) {
          userRepository.updateLastLogin(userId,LocalDateTime.now());
    }

    @Override
    public Boolean checkId(SignUpDTO sign) {
        if (userRepository.findByUserid(sign.getUserId()) == null) {
            return true;
        }
        return false;
    }

    @Override
    public void autoLogin(String userId,String token) {

        redisTemplate.opsForValue().set("RT:" + userId,
                token, Duration.ofDays(7)
        );
    }
    @Override
    public Authentication authenticate(LoginDTO login) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login.getUserId(), login.getPassword());
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

}