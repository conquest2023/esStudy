package es.board.service;

import es.board.model.res.LoginResponse;
import es.board.model.res.SignUpResponse;

public interface UserService {


    void createUser(SignUpResponse sign);
    boolean login(LoginResponse login);

    Boolean checkId(SignUpResponse sign);

    void signUp();
}
