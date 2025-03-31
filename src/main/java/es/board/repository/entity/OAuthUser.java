package es.board.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user_table")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;   // KAKAO, NAVER, GOOGLE

    @Column(nullable = false)
    private String userId;

    private String email;

//    private String nickname;

//    private String profileImage;

    private String username;

    private  String password;

    @Transient
    private  int visitCount;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Column(name = "last_login", columnDefinition = "DATETIME")
    private LocalDateTime lastLogin;


    private LocalDateTime createdAt;




}
