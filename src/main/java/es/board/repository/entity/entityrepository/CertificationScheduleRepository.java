package es.board.repository.entity.entityrepository;

import es.board.repository.entity.CertificationSchedule;
import es.board.repository.entity.Likes;
import es.board.repository.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CertificationScheduleRepository  extends JpaRepository<CertificationSchedule,String> {

    @Query("SELECT t FROM CertificationSchedule t WHERE t.name = :name")
    List<CertificationSchedule> findAllByCertificationSchedule(@Param("name") String name);



    @Query("SELECT DISTINCT t.name FROM CertificationSchedule t WHERE t.majorCategory = :majorCategory AND  t.subCategory=:subCategory")
    List<String> findAllByMajorCategoryAndSubCategory(@Param("majorCategory") String majorCategory,@Param("subCategory")String subCategory);
}
