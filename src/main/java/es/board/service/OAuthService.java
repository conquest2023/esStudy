package es.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.board.controller.model.req.OAuthInfo;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.OAuthSignUp;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Map;

public interface OAuthService {

    Map<String, Object> kakaoLogin(String code) throws JsonProcessingException;

    Map<String, Object> googleLogin(String code) throws IOException;

    String getKaKaoAccessToken(String code) throws JsonProcessingException;

    String getNaverAccessToken(String code,String state) throws JsonProcessingException;

    OAuthInfo getNaverInfo(String accessToken) throws JsonProcessingException;

    OAuthInfo getKakaoInfo(String accessToken) throws JsonProcessingException;

    Authentication authenticate(LoginResponse login);
    void saveUserName(OAuthSignUp sign);
    String getGoogleAccessToken(String code) throws IOException;

    OAuthInfo getGoogleInfo(String accessToken) throws IOException;
    Map<String, Object> naverLogin(String code,String state) throws JsonProcessingException;

}
