package es.board.service;

import es.board.model.res.LoginResponse;

public interface UserService {

    boolean login(LoginResponse login);

    void signUp();
}
