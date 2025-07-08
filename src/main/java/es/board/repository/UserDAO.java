package es.board.repository;

import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
import es.board.repository.entity.User;

public interface UserDAO {

    void createUser(User sign);

    Long findVisitCount(String userId);
    Boolean checkUserId(SignUpDTO sign);
    User login(LoginDTO login);

    void updateVisitCount(String userId);
    void signUp();
}
