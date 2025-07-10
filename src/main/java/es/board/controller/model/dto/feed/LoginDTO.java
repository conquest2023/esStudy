package es.board.controller.model.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {


    private String userId;

    private  boolean autoLogin;

    private String password;

    private  String username;

    private String email;

    private LocalDateTime lastLogin;

}
