package es.board.service;

import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
import org.springframework.security.core.Authentication;

public interface UserService {


    void updateVisitCount(String userId);

    void createUser(SignUpResponse sign);
    boolean login(LoginResponse login);

    Boolean checkId(SignUpResponse sign);

    Long findVisitCount(String  userId);

    Authentication authenticate(LoginResponse login);
    String getUsername(String userId);
}
