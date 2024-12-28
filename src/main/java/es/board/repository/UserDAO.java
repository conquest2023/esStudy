package es.board.repository;

import es.board.model.res.LoginResponse;
import es.board.repository.document.User;

public interface UserDAO {



    User login(LoginResponse login);

    void signUp();
}
