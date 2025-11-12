package es.board.service.point;

public interface PointService {

    void grantActivityPoint(String userId, String activityType, int pointChange, int limitCount);

    void createPointHistory(String userId, int pointChange, String reason);
}
