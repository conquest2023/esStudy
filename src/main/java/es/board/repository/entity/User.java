package es.board.repository.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import jakarta.persistence.Column;
import es.board.controller.model.res.SignUpResponse;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;


@Entity
@Table(name = "user_table")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    private  String userId;

    private String username;

    private String age;

    private String password;


    private  String category;

    @Transient
    private  int visitCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;

    @Column(name = "last_login", columnDefinition = "DATETIME")
    private LocalDateTime lastLogin;

    @ManyToMany(fetch = FetchType.EAGER) // 즉시 로딩하여 권한 정보를 가져옴
    @JoinTable(
            name = "user_roles", // 연결 테이블 이름
            joinColumns = @JoinColumn(name = "user_id"), // 사용자 ID와 매핑
            inverseJoinColumns = @JoinColumn(name = "role_id") // 역할 ID와 매핑
    )

    private Set<Role> roles;


    public void updateLastLogin( LocalDateTime lastLogin) {

        this.lastLogin = lastLogin;
    }
    public User DtoToUser(SignUpResponse sign, String password){

        return  User.builder()
                .id(id)
                .userId(sign.getUserId())
                .username(sign.getUsername())
                .password(password)
                .age(sign.getAge())
                .createdAt(LocalDateTime.now())
                .build();
    }




}
