//package es.board.service.impl;
//
//
//import es.board.config.jwt.JwtTokenProvider;
//import es.board.controller.model.req.ScheduleDTO;
//import es.board.repository.ScheduleDAO;
//import es.board.repository.entity.TeamSchedule;
//import es.board.repository.entity.entityrepository.TeamCalendarRepository;
//import es.board.service.TeamCalenderService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//@Service
//@RequiredArgsConstructor
//public class TeamCalenderServiceImpl  implements TeamCalenderService {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final TeamCalendarRepository teamCalendarRepository;
////    private final ScheduleMapper scheduleMapper;
//    private final ScheduleDAO scheduleDAO;
//    private final AsyncService asyncService;
//
//    /**
//     * ✅ 팀 일정 저장 (일반 일정)
//     */
//    @Transactional
//    public void saveSchedule(Long teamId, Long userId, ScheduleDTO scheduleDTO) {
//        CompletableFuture.supplyAsync(() -> {
//            Long saveScheduleId = getSaveScheduleId(teamId, userId, scheduleDTO);
//
//            // ✅ Elasticsearch에 저장
//            asyncService.saveScheduleAsync(scheduleMapper.toScheduleDocument(teamId, userId, scheduleDTO, saveScheduleId));
//
//            return null;
//        });
//    }
//
//    /**
//     * ✅ 반복 일정 저장
//     */
//    @Transactional
//    public void saveRepeatSchedule(Long teamId, Long userId, ScheduleDTO scheduleDTO) {
//        CompletableFuture.supplyAsync(() -> {
//            List<TeamSchedule> schedulesToInsert = scheduleMapper.generateRepeatSchedules(teamId, userId, scheduleDTO);
//
//            List<TeamSchedule> savedSchedules = teamCalendarRepository.saveAll(schedulesToInsert);
//
//            List<es.board.repository.document.Schedule> scheduleDocuments = scheduleMapper.toScheduleDocumentList(teamId, userId, savedSchedules);
//
//            asyncService.saveScheduleBulkAsync(scheduleDocuments);
//            return null;
//        });
//    }
//
//    /**
//     * ✅ 팀 일정 조회 (일반 일정 + 반복 일정)
//     */
//    @Transactional(readOnly = true)
//    public List<ScheduleDTO> getSchedulesByTeam(Long teamId) {
//        return scheduleMapper.fromSchedule(teamCalendarRepository.findAllByTeamId(teamId));
//    }
//
//    /**
//     * ✅ 반복 일정 조회
//     */
////    @Transactional(readOnly = true)
////    public List<ScheduleDTO> getRepeatSchedules(Long teamId) {
////        return scheduleMapper.fromSchedule(teamCalendarRepository.findDistinctRepeatSchedules(teamId));
////    }
//
//    /**
//     * ✅ 일정 검색 (Elasticsearch 연동)
//     */
////    @Transactional(readOnly = true)
////    public List<ScheduleDTO> searchSchedule(Long teamId, String title, String description, String category) {
////        return scheduleMapper.fromScheduleDocument(scheduleDAO.searchScheduleByTeam(teamId, title, description, category));
////    }
////
////    /**
////     * ✅ 팀 일정 삭제
////     */
////    @Transactional
////    public void deleteSchedule(Long teamId, Long scheduleId) {
////        teamCalendarRepository.deleteById(scheduleId);
////
////        // ✅ Elasticsearch에서도 삭제
////        CompletableFuture.runAsync(() -> {
////            scheduleDAO.deleteSchedule(scheduleId);
////        });
////    }
////
////    /**
////     * ✅ 반복 일정 삭제
////     */
////    @Transactional
////    public void deleteRepeatSchedule(Long teamId, LocalDateTime createdAt, LocalDateTime start, LocalDateTime end) {
////        teamCalendarRepository.deleteByTeamIdAndCreatedAtAndIsRepeat(teamId, createdAt);
////
////        CompletableFuture.runAsync(() -> {
////            scheduleDAO.deleteRepeatScheduleByTeam(teamId, start, end);
////        });
////    }
////
////    /**
////     * ✅ 일정 저장 시 ID 반환
////     */
//    private Long getSaveScheduleId(Long teamId, Long userId, ScheduleDTO scheduleDTO) {
//        TeamSchedule savedSchedule = teamCalendarRepository.save(scheduleMapper.toTeamScheduleEntity(teamId, userId, scheduleDTO));
//        return savedSchedule.getScheduleId();
//    }
//}
