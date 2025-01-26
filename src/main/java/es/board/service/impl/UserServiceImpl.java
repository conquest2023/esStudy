package es.board.service.impl;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.mapper.CommentMapper;
import es.board.controller.model.req.CommentRequest;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.CommentDAO;
import es.board.repository.FeedDAO;
import es.board.repository.UserDAO;
import es.board.repository.document.EsUser;
import es.board.repository.entity.Post;
import es.board.repository.entity.entityrepository.PostRepository;
import es.board.repository.entity.entityrepository.UserRepository;
import es.board.repository.entity.User;
import es.board.service.FeedService;
import es.board.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    private  final  JwtTokenProvider jwtTokenProvider;

    private  final  FeedDAO feedDAO;

    private  final CommentDAO commentDAO;

    private  final AuthenticationManagerBuilder authenticationManagerBuilder;

    private  final UserRepository userRepository;

    private final CommentMapper commentMapper;

    private  final UserDAO userDAO;

    private  final  AsyncService asyncService;

    private  final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(SignUpResponse sign) {

        User user=new User();
        String password=passwordEncoder.encode(sign.getPassword());
        asyncService.saveUserAsync(sign,password);
        userRepository.save(user.DtoToUser(sign, password));

    }
    @Override
    public void updateVisitCount(String userId) {
        userDAO.modifyVisitCount(userId);
    }
    @Override
    @Transactional
    public boolean login(LoginResponse login) {

        userRepository.updateLastLogin(login.getUserId(),LocalDateTime.now());
        UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(login.getUserId(),login.getPassword());
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return  true;}


    @Override
    public Boolean checkId(SignUpResponse sign) {
        if(userRepository.findByUserid(sign.getUserId())==null){
            return  true;
        }
            return  false;}

    @Override
    public boolean checkFeedOwner(String token,String feedUID) {
        if (jwtTokenProvider.getUserId(token).equals(feedDAO.findFeedDetail(feedUID).getUserId())){
            return  true;
        }
            return  false;
    }

    @Override
    public List<CommentRequest> getCommentOwnerList(String commentOwner,String feedUID, String userId) {
        return commentMapper.changeCommentListDTO(commentDAO.findCommentId(feedUID))
                .stream()
                .peek(comment -> {
                    comment.setAuthor(comment.getUserId().equals(userId));
                    comment.setCommentOwner(comment.getUserId().equals(commentOwner));
                })
                .collect(Collectors.toList());
    }



    @Override
    public Long findVisitCount(String userId) {
        return  userDAO.findVisitCount(userId);
    }



    @Override
    public Authentication authenticate(LoginResponse login) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login.getUserId(), login.getPassword());

        // 인증 수행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return authentication;
    }


    @Override
    public String getUsername(String userId) {

        return  userRepository.findByUsername(userId);
    }

}
