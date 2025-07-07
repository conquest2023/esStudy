package es.board.service;

import es.board.controller.model.req.FeedDTO;
import es.board.controller.model.res.CommentDTO;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface AuthService {


    String findById(String  username);
    Boolean extractUserIdFromToken(String token, FeedDTO.Response response);


    void updateVisitCount(String userId);

    void createUser(SignUpResponse sign);
    boolean login(LoginResponse login);

    Boolean checkId(SignUpResponse sign);
    List<CommentDTO.Request> getCommentOwnerList(Object comments, String commentOwner, String  feedUID, String userId);
    Long findVisitCount(String  userId);

    Authentication authenticate(LoginResponse login);


}
