package es.board.service.impl;

import es.board.model.res.LoginResponse;
import es.board.model.res.SignUpResponse;
import es.board.repository.UserDAO;
import es.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private  final UserDAO userDAO;

    @Override
    public SignUpResponse createUser(SignUpResponse sign) {
       return userDAO.createUser(sign);
    }

    @Override
    public boolean login(LoginResponse login) {

        if(userDAO.login(login)==null){
            return  false;
        }else{
            return true;
        }
    }

    @Override
    public Boolean checkId(SignUpResponse sign) {
        return userDAO.checkUserId(sign);
    }

    @Override
    public void signUp() {

    }
}
