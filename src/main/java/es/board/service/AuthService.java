package es.board.service;

import es.board.controller.model.dto.feed.FeedDTO;
import es.board.controller.model.dto.feed.CommentDTO;
import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AuthService {


    String findById(String  username);
    Boolean extractUserIdFromToken(String token, FeedDTO.Response response);


    void  autoLogin(String userId, String token);

    void updateVisitCount(String userId);

    void createUser(SignUpDTO sign);
    boolean login(LoginDTO login);

    Boolean checkId(SignUpDTO sign);
    List<CommentDTO.Request> getCommentOwnerList(Object comments, String commentOwner, String  feedUID, String userId);
    Long findVisitCount(String  userId);

    Authentication authenticate(LoginDTO login);


}
