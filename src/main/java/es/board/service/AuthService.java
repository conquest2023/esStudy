package es.board.service;

import es.board.controller.model.req.CommentRequest;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AuthService {



    Boolean extractUserIdFromToken(String token, FeedCreateResponse response);


    void updateVisitCount(String userId);

    void createUser(SignUpResponse sign);
    boolean login(LoginResponse login);

    Boolean checkId(SignUpResponse sign);
    List<CommentRequest> getCommentOwnerList(Object comments, String commentOwner, String  feedUID,String userId);
    Long findVisitCount(String  userId);

    Authentication authenticate(LoginResponse login);


}
