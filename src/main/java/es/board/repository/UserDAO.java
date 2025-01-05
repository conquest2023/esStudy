package es.board.repository;

import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.entity.User;

public interface UserDAO {

    void createUser(User sign);

    Long findVisitCount(String userId);
    Boolean checkUserId(SignUpResponse sign);
    User login(LoginResponse login);

    void modifyVisitCount(String userId);
    void signUp();
}
