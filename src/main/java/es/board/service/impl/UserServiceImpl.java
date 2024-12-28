package es.board.service.impl;

import es.board.model.res.LoginResponse;
import es.board.repository.UserDAO;
import es.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private  final UserDAO userDAO;
    @Override
    public boolean login(LoginResponse login) {

        if(userDAO.login(login)==null){
            return  false;
        }else{
            return true;
        }
    }

    @Override
    public void signUp() {

    }
}
