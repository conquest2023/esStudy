package es.board.repository;


import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface VisitDAO {

    Map<String, Long> findVisitorStats();
    CompletableFuture<Void> saveIp(String userId, String ip, String sessionId, String userAgent);
}
