package es.board.service.point;

import es.board.controller.model.dto.feed.TopWriter;

import java.util.List;

public interface PointService {

    void grantActivityPoint(String userId, String activityType, int pointChange, int limitCount);

    List<TopWriter> getSumTop5User();
    void createPointHistory(String userId, int pointChange, String reason);
}
