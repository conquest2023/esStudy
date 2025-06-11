package es.board.ex;

import es.board.config.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
public class TokenValidator {

    private  final JwtTokenProvider jwtTokenProvider;

    public TokenValidator(JwtTokenProvider jwtTokenProvider){

        this.jwtTokenProvider=jwtTokenProvider;
    }

    public ResponseEntity<?> validateTokenOrRespond(String token) {


        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "토큰이 필요합니다."));
        }

        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtTokenProvider.validateToken(rawToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "세션이 만료되었습니다."));
        }
        return null;
    }

}
