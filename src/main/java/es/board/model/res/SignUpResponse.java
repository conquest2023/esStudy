package es.board.model.res;


import es.board.repository.document.EsUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {

    private String userId;

    private  String username;

    private  String age;

    private String password;


    private  String category;


}
