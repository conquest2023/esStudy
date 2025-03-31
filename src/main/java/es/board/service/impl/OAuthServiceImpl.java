package es.board.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.controller.model.req.KakaoInfo;
import es.board.controller.model.res.LoginResponse;
import es.board.controller.model.res.OAuthSignUp;
import es.board.repository.entity.OAuthUser;
import es.board.repository.entity.entityrepository.OAuthRepository;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {


    private  final OAuthRepository oAuthRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final PasswordEncoder passwordEncoder;



    @Value("${kakao.client.id}")
    private String clientId;
    @Value("${kakao.redirect.uri}")
    private String redirectUri;

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

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("access_token").asText();
    }

    public KakaoInfo getKakaoInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보 꺼내기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String password=passwordEncoder.encode(id.toString());

        return new KakaoInfo(id, nickname, email,password);
    }

    @Override
    public void saveUserName(OAuthSignUp sign) {
        oAuthRepository.updateOAuthUserByUsername(sign.getUsername() ,sign.getProvider(),sign.getEmail());
    }

    @Override
    public Map<String, Object> login(String code) throws JsonProcessingException {
        String accessToken = getKaKaoAccessToken(code);
        KakaoInfo kakaoInfo = getKakaoInfo(accessToken);
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
//                    .nickname(kakaoInfo.getNickname())
//                    .profileImage(null)
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
