package es.board.service;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
import es.board.repository.entity.repository.infrastructure.projection.UserPointProjection;
import es.board.repository.entity.repository.infrastructure.projection.UserPointSummary;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AuthService {


    String findById(String  username);
    Boolean extractUserIdFromToken(String token, PostDTO.Response response);


    List<UserPointProjection> getSumPointUser();
    void  autoLogin(String userId, String token);

    void createUser(SignUpDTO sign);
    boolean login(LoginDTO login);

    Boolean checkId(SignUpDTO sign);

    Authentication authenticate(LoginDTO login);


}
