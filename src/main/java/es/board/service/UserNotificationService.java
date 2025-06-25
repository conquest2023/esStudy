package es.board.service;

import es.board.repository.entity.Notification;

import java.util.List;

public interface UserNotificationService {


    void deleteNotification(String  userId, List<String> id);


    void checkedNotification(String  userId, List<String> id);




    List<Notification> getCheckNotifications(String  userId);

}
