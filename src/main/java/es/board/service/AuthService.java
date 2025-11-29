package es.board.service;

import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
import es.board.infrastructure.entity.user.User;
import org.springframework.security.core.Authentication;

public interface AuthService {

//    List<UserPointProjection> getSumPointUser();
     User findByUser(String  username);

    void updateLastLogin(String userId);

    void  autoLogin(String userId, String token);

    void registerUser(SignUpDTO sign);
    boolean login(LoginDTO login);

    Boolean checkId(SignUpDTO sign);

    Authentication authenticate(LoginDTO login);


}
