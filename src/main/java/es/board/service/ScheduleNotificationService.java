package es.board.service;

public interface ScheduleNotificationService {

    void sendTop3Hourly();

    void sendRank1stEvery2h();

    void sendPoll();
}
