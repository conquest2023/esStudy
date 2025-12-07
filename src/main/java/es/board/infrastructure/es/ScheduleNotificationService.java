package es.board.infrastructure.es;

public interface ScheduleNotificationService {

    void sendTop3Hourly();

    void sendRank1stEvery4h();

    void sendPoll();


    void sendAnalysisUserHistory();
}
