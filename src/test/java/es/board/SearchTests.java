package es.board;


import es.board.repository.ScheduleDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SearchTests {

    private final ScheduleDAO scheduleDAO;

    @Autowired  // ✅ Spring이 자동 주입
    public SearchTests(ScheduleDAO scheduleDAO) {
        this.scheduleDAO = scheduleDAO;
    }

    @Test
    void  searchCalender(){


        System.out.println("scheduleDAO = " + scheduleDAO.searchSchedule("asd","title" ,"zzzz","soonest"));
    }
}
