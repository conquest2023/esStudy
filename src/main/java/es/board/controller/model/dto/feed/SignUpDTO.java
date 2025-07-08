package es.board.controller.model.dto.feed;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.board.filter.XssSafeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDTO {

    private String userId;

    private  String username;

    private  String age;

    private String interest;

    private String password;

    @JsonSerialize(using = XssSafeSerializer.class)
    private  String category;

    private  int visitCount;

    private  String  role;


}
