package es.board.controller.model.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {


    private String userId;

    private String password;

    private  String username;

    private String email;

    private LocalDateTime lastLogin;


    public  LoginResponse(String username, String email){
        this.username=username;
        this.email=email;
    }
}
