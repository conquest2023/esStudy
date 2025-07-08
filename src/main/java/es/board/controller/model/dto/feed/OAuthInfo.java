package es.board.controller.model.dto.feed;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthInfo {

    private String id;

    private String nickname;

    private String email;

    private  String password;

    public OAuthInfo(String id, String nickname, String email, String password) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password=password;
    }
}