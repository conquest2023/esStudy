package es.board.infrastructure.es.document;

import java.time.LocalDateTime;
import java.util.List;

public interface ViewDAO {




    List<View> findUserDailyViewHistory(String userId,
                                        LocalDateTime ago);

    List<View> findUsersDailyViewHistorys(List<String> userIds, LocalDateTime ago);
}
