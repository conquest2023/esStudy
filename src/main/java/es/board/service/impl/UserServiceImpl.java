package es.board.service.impl;

import es.board.model.res.LoginResponse;
import es.board.model.res.SignUpResponse;
import es.board.repository.UserDAO;
import es.board.repository.UserRepository;
import es.board.repository.document.EsUser;
import es.board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private  final UserDAO userDAO;

    private  final UserRepository userRepository;

    @Override
    public void createUser(SignUpResponse sign) {

        EsUser user=new EsUser();
        userRepository.save(user.DtoToUser(sign));

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
        log.info(sign.toString());
        if(userRepository.findByUserId(sign.getUserId())==null){
            return  true;
        }else {
            return  false;
        }
    }

    @Override
    public void signUp() {

    }
}
