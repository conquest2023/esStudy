package es.board.infrastructure.es;

import es.board.infrastructure.es.document.View;

import java.time.LocalDateTime;
import java.util.List;

public interface ViewEventService {


    List<View> getUserDailyViewHistory(String userId, LocalDateTime ago);



    List<View> getUsersDailyViewHistorys(List<String> userIds, LocalDateTime ago);

}
