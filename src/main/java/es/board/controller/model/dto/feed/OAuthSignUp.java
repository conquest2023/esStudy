package es.board.controller.model.dto.feed;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthSignUp {

    private String username;

    private String  providerId;

    private String provider;

    private String email;

}
