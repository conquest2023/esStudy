package es.board.repository;

import es.board.model.res.LoginResponse;
import es.board.model.res.SignUpResponse;
import es.board.repository.document.EsUser;

public interface UserDAO {

    SignUpResponse createUser(SignUpResponse sign);
    Boolean checkUserId(SignUpResponse sign);
    EsUser login(LoginResponse login);

    void signUp();
}
