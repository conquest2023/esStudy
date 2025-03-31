//package es.board.config;
//
//import es.board.controller.model.req.KakaoDTO;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//@Component
//public class KaKaoConfig {
//
//    @Value("${spring.kakao.auth.client}")
//    private String client;
//    @Value("${spring.kakao.auth.redirect}")
//    private String redirect;
//
//    public KakaoDTO.OAuthToken requestToken(String accessCode) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", client);
//        params.add("redirect_url", redirect);
//        params.add("code", accessCode);
//    }
//}
