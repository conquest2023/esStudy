package es.board.service.impl;

import es.board.repository.entity.Notification;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private  final NotificationRepository notificationRepository;
    @Override
    public void deleteNotification(String userId, List<Long> id) {
        notificationRepository.deleteById(userId,id);
    }

    @Override
    public void checkedNotification(String userId, List<Long> id) {

        notificationRepository.checkId(userId,id);
    }

    @Override
    public int getCountCheckNotification(String userId) {
        return  notificationRepository.countByIsCheckNotification(userId);
    }

    @Override
    public List<Notification> getCheckNotifications(String userId) {

      return    notificationRepository.findByCheckNotification(userId);
    }


}
