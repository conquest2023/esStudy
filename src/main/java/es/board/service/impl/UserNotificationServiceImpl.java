package es.board.service.impl;

import es.board.ex.IndexException;
import es.board.repository.entity.Notification;
import es.board.repository.entity.repository.NotificationRepository;
import es.board.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private  final NotificationRepository notificationRepository;
    @Override
    public void deleteNotification(String userId, List<Long> id) {
        try {
            notificationRepository.deleteById(userId,id);
        }catch (IndexException e){
            throw new IndexException("알림 읽음 처리 중 오류 발생", e);
        }
    }

    @Override
    public void checkedNotification(String userId, List<Long> id) {
        try {
            notificationRepository.checkNotification(userId,id, LocalDateTime.now());
        }catch (IndexException e){
            throw new IndexException("알림 읽음 처리 중 오류 발생", e);
        }
    }

    @Override
    public int getCountCheckNotification(String userId) {
        try {
            return  notificationRepository.countByIsCheckNotification(userId);
        }catch (IndexException e){
            throw new IndexException("알림 읽음 처리 중 오류 발생", e);
        }
    }

    @Override
    public List<Notification> getRecentNotifications(String userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        try {
            return notificationRepository.findByRecentNotification(userId,sevenDaysAgo);
        }catch (IndexException e){
            throw new IndexException("알림 읽음 처리 중 오류 발생", e);
        }
    }


}
