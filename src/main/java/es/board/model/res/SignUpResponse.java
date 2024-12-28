package es.board.model.res;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {

    private String userId;

    private  String username;

    private int age;

    private String password;


    private  String category;
}
