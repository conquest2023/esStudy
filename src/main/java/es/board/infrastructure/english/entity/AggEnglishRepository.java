package es.board.infrastructure.english.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AggEnglishRepository extends JpaRepository<EnglishProblemAttempt, Long>  {

    @Query("select count(*) from EnglishProblemAttempt e where e.userId=:userId" +
            " and e.part=:part" +
            " and e.createdAt>=:now ")
    Integer countByAttemptVocab(String userId,String part,LocalDateTime now);



    @Query("select count(*) from EnglishProblemAttempt e where e.userId=:userId " +
            "and e.part=:part" +
            " and e.createdAt>=:now ")
    Integer countByAttemptRc(String userId, String part , LocalDateTime now);
}
