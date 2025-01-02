package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.entity.entityrepository.UserRepository;
import es.board.repository.entity.EsUser;
import es.board.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;
    private  final AuthenticationManagerBuilder authenticationManagerBuilder;

    private  final JwtTokenProvider jwtTokenProvider;

    private  final UserRepository userRepository;

    private  final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(SignUpResponse sign) {

        EsUser user=new EsUser();
        String password=passwordEncoder.encode(sign.getPassword());
        userRepository.save(user.DtoToUser(sign, password));
    }

    @Override
    @Transactional
    public boolean login(LoginResponse login) {

//        return userRepository.existsByUserIdAndPassword(login.getUserId(),login.getPassword());
//        if(!userRepository.existsByUserIdAndPassword(login.getUserId(),login.getPassword())) {
//            return  false;
//        }else{
        userRepository.updateLastLogin(login.getUserId(),LocalDateTime.now());
        UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(login.getUserId(),login.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return  true;
        }


    @Override
    public Boolean checkId(SignUpResponse sign) {
        log.info(sign.toString());
        if(userRepository.findByUserid(sign.getUserId())==null){
            return  true;
        }else {
            return  false;
        }
    }

    @Override
    public Authentication authenticate(LoginResponse login) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login.getUserId(), login.getPassword());

        // 인증 수행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        log.info("Authorities: {}", authentication.getAuthorities());

        return authentication;
    }

    @Override
    public String findIdOne() {
        return null;
    }

}
