package es.board.service.impl;

import es.board.repository.VisitDAO;
import es.board.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private  final VisitDAO  visitDAO;


    @Override
    public Map<String, Long> getStats() {
        return visitDAO.findVisitorStats();
    }

    @Override
    public void saveIP(String userId, String ip,String sessionId,String userAgent) {
            visitDAO.saveIp(userId,ip,sessionId,userAgent);

    }
}
