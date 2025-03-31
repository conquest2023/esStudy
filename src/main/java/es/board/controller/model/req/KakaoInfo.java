package es.board.controller.model.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoInfo {

    private Long id;

    private String nickname;

    private String email;

    private  String password;

    public KakaoInfo(Long id, String nickname, String email, String password) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password=password;
    }
}