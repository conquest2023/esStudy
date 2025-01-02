package es.board.controller.model.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String userId;

    private String password;


    private LocalDateTime lastLogin;
}
