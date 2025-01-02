package es.board.service.impl;

import es.board.repository.entity.entityrepository.UserRepository;
import es.board.repository.entity.EsUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailServiceImpl implements UserDetailsService {

    private  final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

      EsUser user= userRepository.findByUserId(username)
//                .map(this::createUserDetails)
                .orElseThrow(()->new UsernameNotFoundException("해당하는 회원을 찾을수 없다"));
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
        log.info(authorities.toString());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    public   String createUserDetails(String username){
        String user=userRepository.findByUserIdOne(username);

        return user;
    }


    private  UserDetails createUserDetails(EsUser user){
        return User.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }
}
