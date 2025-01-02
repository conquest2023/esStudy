package es.board.repository;

import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.entity.EsUser;

public interface UserDAO {

    SignUpResponse createUser(SignUpResponse sign);
    Boolean checkUserId(SignUpResponse sign);
    EsUser login(LoginResponse login);

    void signUp();
}
