package es.board.service;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
import es.board.repository.entity.repository.infrastructure.projection.UserPointProjection;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AuthService {

    List<UserPointProjection> getSumPointUser();
    String findById(String  username);

    void  autoLogin(String userId, String token);

    void registerUser(SignUpDTO sign);
    boolean login(LoginDTO login);

    Boolean checkId(SignUpDTO sign);

    Authentication authenticate(LoginDTO login);


}
