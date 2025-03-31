package es.board.repository.entity.entityrepository;


import es.board.repository.entity.OAuthUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OAuthRepository  extends JpaRepository<OAuthUser,Long> {



    @Query("SELECT u FROM OAuthUser u  where u.userId=:userId")
    Optional<OAuthUser> findByProviderAndUserId(@Param("userId") String userId);


    @Modifying
    @Transactional
    @Query("UPDATE OAuthUser u SET u.username = :username WHERE u.provider = :provider AND u.email = :email")
    void updateOAuthUserByUsername(@Param("username") String username, @Param("provider") String provider,@Param("email") String email);
}