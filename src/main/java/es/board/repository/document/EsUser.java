package es.board.repository.document;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import es.board.controller.model.res.SignUpResponse;
import es.board.repository.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "user")
public class EsUser {

    @Id
    @GeneratedValue
    private  String id;

    private  String userId;

    private String username;


    private String password;

    private String  age;

    private  Integer visitCount;


    private  String category;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Column(name = "modified_at")
    private LocalDateTime updatedAt;


//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
//    private LocalDateTime deletedAt;

    public EsUser DtoToEsUser(SignUpResponse sign){
        return  EsUser.builder()
                .id(id)
                .userId(sign.getUserId())
                .username(sign.getUsername())
                .age(sign.getAge())
                .createdAt(LocalDateTime.now())
                .build();
    }

}
