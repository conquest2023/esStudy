package es.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.board.controller.model.req.KakaoInfo;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.OAuthSignUp;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface OAuthService {

    Map<String, Object> login(String code) throws JsonProcessingException;

    String getKaKaoAccessToken(String code) throws JsonProcessingException;

    KakaoInfo getKakaoInfo(String accessToken) throws JsonProcessingException;

    Authentication authenticate(LoginResponse login);
    void saveUserName(OAuthSignUp sign);

}
