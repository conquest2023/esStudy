package es.board.service;

import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
import org.springframework.security.core.Authentication;

public interface UserService {


    void createUser(SignUpResponse sign);
    boolean login(LoginResponse login);

    Boolean checkId(SignUpResponse sign);



    Authentication authenticate(LoginResponse login);
    String findIdOne();
}
