package es.board.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.req.OAuthInfo;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.OAuthSignUp;
import es.board.repository.entity.OAuthUser;
import es.board.repository.entity.repository.OAuthRepository;
import es.board.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {


    private  final ObjectMapper objectMapper;

//    private  final RestTemplate restTemplate;

    private  final OAuthRepository oAuthRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final PasswordEncoder passwordEncoder;



    @Value("${kakao.client.id}")
    private String clientId;
    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${naver.client.id}")
    private String naverClientId;
    @Value("${naver.client.secret}")
    private String naverClientSecret;
    @Value("${naver.redirect.uri}")
    private String navarRedirectUri;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    @Value("${google.redirect.uri}")
    private String googleRedirectUri;
    @Override
    public String getGoogleAccessToken(String code) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", googleRedirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                request,
                String.class
        );

        JsonNode json = objectMapper.readTree(response.getBody());
        return json.get("access_token").asText();
    }

    @Override
    public OAuthInfo getGoogleInfo(String accessToken) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                request,
                String.class
        );

        JsonNode userJson = objectMapper.readTree(response.getBody());
        String id = userJson.get("sub").asText();
        String email = userJson.get("email").asText();
        String name = userJson.get("name").asText();
        String Password = passwordEncoder.encode(id);

        return new OAuthInfo(id, name, email, Password);
    }
    @Override
    public String getNaverAccessToken(String code,String state) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naverClientId);
        body.add("client_secret",naverClientSecret);
        body.add("redirect_uri", navarRedirectUri);
        body.add("code", code);
        body.add("state",state);
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );
        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    public OAuthInfo getNaverInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<Void> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                naverUserInfoRequest,
                String.class
        );
        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode responseNode = jsonNode.get("response");
        log.info(responseNode.toString());
        String id = responseNode.get("id").asText();
        String email =   responseNode.get("email").asText();
        String name = responseNode.get("name").asText();
        String password=passwordEncoder.encode(id.toString());
        return new OAuthInfo(id, name, email,password);
    }

    @Override
    public String getKaKaoAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("access_token").asText();
    }

    public OAuthInfo getKakaoInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String id =jsonNode.get("id").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String password=passwordEncoder.encode(id.toString());
        log.info(jsonNode.toString());
        return new OAuthInfo(id, nickname, email,password);
    }

    @Override
    public void saveUserName(OAuthSignUp sign) {
        oAuthRepository.updateOAuthUserByUsername(sign.getUsername() ,sign.getProvider(),sign.getEmail());
    }

    @Override
    public Map<String, Object> googleLogin(String code) throws IOException {
        String accessToken = getGoogleAccessToken(code);
        OAuthInfo googleInfo = getGoogleInfo(accessToken);
        Optional<OAuthUser> existingUser = oAuthRepository.findByProviderAndUserId(googleInfo.getId().toString());
        OAuthUser user;
        Map<String, Object> result = new HashMap<>();
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setLastLogin(LocalDateTime.now());
            result.put("provider", user.getProvider());
            result.put("providerId", user.getUserId());
            result.put("email",user.getEmail());
            result.put("password",user.getPassword());
            result.put("result", "ok");
            result.put("isNewUser", false);
        } else {
            user = OAuthUser.builder()
                    .provider("GOOGLE")
                    .userId(googleInfo.getId().toString())
                    .email(googleInfo.getEmail())
                    .password(googleInfo.getPassword())
                    .createdAt(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .build();
            oAuthRepository.save(user);
            log.info(user.toString());
            result.put("result", "newUser");
            result.put("provider", user.getProvider());
            result.put("providerId", user.getUserId());
            result.put("password",user.getPassword());
            result.put("email",user.getEmail());
            result.put("isNewUser", true);
        }
        return result;
    }

    @Override
    public Map<String, Object> kakaoLogin(String code) throws JsonProcessingException {
        String accessToken = getKaKaoAccessToken(code);
        OAuthInfo kakaoInfo = getKakaoInfo(accessToken);
        Optional<OAuthUser> existingUser = oAuthRepository.findByProviderAndUserId(kakaoInfo.getId().toString());
        OAuthUser user;
        Map<String, Object> result = new HashMap<>();
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setLastLogin(LocalDateTime.now());
            result.put("provider", user.getProvider());
            result.put("providerId", user.getUserId());
            result.put("email",user.getEmail());
            result.put("password",user.getPassword());
            result.put("result", "ok");
            result.put("isNewUser", false);
        } else {
            user = OAuthUser.builder()
                    .provider("KAKAO")
                    .userId(kakaoInfo.getId().toString())
                    .email(kakaoInfo.getEmail())
                    .password(kakaoInfo.getPassword())
                    .createdAt(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .build();
            oAuthRepository.save(user);
            log.info(user.toString());
            result.put("result", "newUser");
            result.put("provider", user.getProvider());
            result.put("providerId", user.getUserId());
            result.put("password",user.getPassword());
            result.put("email",user.getEmail());
            result.put("isNewUser", true);
        }
        return result;
    }


    @Override
    public Map<String, Object> naverLogin(String code,String state) throws JsonProcessingException {
        String accessToken = getNaverAccessToken(code,state);
        OAuthInfo naverInfo = getNaverInfo(accessToken);
        Optional<OAuthUser> existingUser = oAuthRepository.findByProviderAndUserId(naverInfo.getId().toString());
        OAuthUser user;
        Map<String, Object> result = new HashMap<>();
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setLastLogin(LocalDateTime.now());
            result.put("provider", user.getProvider());
            result.put("providerId", user.getUserId());
            result.put("email",user.getEmail());
            result.put("password",user.getPassword());
            result.put("result", "ok");
            result.put("isNewUser", false);
        } else {
            user = OAuthUser.builder()
                    .provider("NAVER")
                    .userId(naverInfo.getId().toString())
                    .email(naverInfo.getEmail())
                    .password(naverInfo.getPassword())
                    .createdAt(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .build();
            oAuthRepository.save(user);
            log.info(user.toString());
            result.put("result", "newUser");
            result.put("provider", user.getProvider());
            result.put("providerId", user.getUserId());
            result.put("password",user.getPassword());
            result.put("email",user.getEmail());
            result.put("isNewUser", true);
        }
        return result;
    }

    @Override
    public Authentication authenticate(LoginResponse login) {
        log.info(login.toString());
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login.getUserId(),login.getUserId());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return authentication;
    }
}
