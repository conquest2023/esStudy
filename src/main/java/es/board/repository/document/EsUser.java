package es.board.repository.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import jakarta.persistence.Column;
import es.board.model.res.SignUpResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;


@Entity
@Table(name = "user_table")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EsUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    private  String userId;

    private String username;

    private String age;

    private String password;


    private  String category;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;



    public EsUser DtoToUser(SignUpResponse sign){
        return  EsUser.builder()
                .id(id)
                .userId(sign.getUserId())
                .username(sign.getUsername())
                .password(sign.getPassword())
                .age(sign.getAge())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
