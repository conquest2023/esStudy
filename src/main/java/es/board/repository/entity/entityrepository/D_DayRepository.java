package es.board.repository.entity.entityrepository;

import es.board.repository.entity.D_Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface D_DayRepository  extends JpaRepository<D_Day,Long> {

    @Query("select p from D_Day p where p.userId=:userId")
    List<D_Day> findAll(@Param("userId") String userId);

}
