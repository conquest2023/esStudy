package es.board.service;

import java.util.Map;

public interface VisitorService {


    Map<String,Long> getStats();
    void saveIP(String userId, String ip,String userAgent);
}
