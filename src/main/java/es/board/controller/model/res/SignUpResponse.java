package es.board.controller.model.res;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {

    private String userId;

    private  String username;

    private  String age;

    private String password;


    private  String category;

    private  String  role;


}
