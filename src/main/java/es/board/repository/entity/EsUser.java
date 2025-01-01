package es.board.repository.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import jakarta.persistence.Column;
import es.board.model.res.SignUpResponse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "user_table")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EsUser implements UserDetails {



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

    @ManyToMany(fetch = FetchType.EAGER) // 즉시 로딩하여 권한 정보를 가져옴
    @JoinTable(
            name = "user_roles", // 연결 테이블 이름
            joinColumns = @JoinColumn(name = "user_id"), // 사용자 ID와 매핑
            inverseJoinColumns = @JoinColumn(name = "role_id") // 역할 ID와 매핑
    )
    private Set<Role> roles;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  null;
//        return this.roles.stream()
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public EsUser DtoToUser(SignUpResponse sign, String password){
        Role defaultRole = new Role();
        defaultRole.setName("ROLE_USER");
        return  EsUser.builder()
                .id(id)
                .userId(sign.getUserId())
                .username(sign.getUsername())
                .password(password)
                .age(sign.getAge())
//                .roles(Set.of(defaultRole))
                .createdAt(LocalDateTime.now())
                .build();
    }

}
