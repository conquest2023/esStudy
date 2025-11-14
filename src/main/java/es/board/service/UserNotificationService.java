package es.board.service;

import es.board.repository.entity.Notification;

import java.util.List;

public interface UserNotificationService {


    void deleteNotification(String  userId, List<Long> id);


    void checkedNotification(String  userId, List<Long> id);


    int getCountCheckNotification(String userId);

    List<Notification> getRecentNotifications(String  userId);

}
