package es.board.service;

import java.time.LocalDate;

public interface ScheduleNotificationService {

    void sendTop3Hourly();

    void sendRank1stEvery2h();

    void  flushQuietHoursQueue();
}
