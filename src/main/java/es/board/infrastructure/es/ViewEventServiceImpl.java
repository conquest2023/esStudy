package es.board.infrastructure.es;

import es.board.infrastructure.es.document.View;
import es.board.infrastructure.es.document.ViewLogDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewEventServiceImpl implements ViewEventService{

    private final ViewLogDAO viewDAO;
    @Override
    public List<View> getUserDailyViewHistory(String userId, LocalDateTime ago) {

        return viewDAO.findUserDailyViewHistory(userId,ago);
    }

    @Override
    public List<View> getUsersDailyViewHistorys(List<String> userIds, LocalDateTime ago) {
        return viewDAO.findUsersDailyViewHistorys(userIds,LocalDateTime.now());
    }
}
