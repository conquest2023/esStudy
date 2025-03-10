package es.board.repository.entity.entityrepository;

import es.board.repository.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository  extends JpaRepository<Vote,Long> {


    @Query(" select p from Vote p where p.id=:id")
    Vote findByVoteId(@Param("id") Long id);


    @Query("SELECT p FROM Vote p  ORDER BY p.createdAt DESC LIMIT 1")
    Vote findLatestVote();

}
