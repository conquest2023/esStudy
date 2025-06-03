package es.board.repository.entity.repository;

import es.board.repository.entity.UserVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteUserRepository extends JpaRepository<UserVote, Long> {

}
