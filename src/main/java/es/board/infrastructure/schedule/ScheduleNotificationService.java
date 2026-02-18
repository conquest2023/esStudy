package es.board.infrastructure.schedule;

public interface ScheduleNotificationService {

    void sendTop3Hourly();


    void sendEnglishPractice();
    void sendRank1stEvery4h();

    void sendPoll();


    void sendAnalysisUserHistory();
}
