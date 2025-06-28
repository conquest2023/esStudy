package es.board.controller.model.mapper;


import es.board.controller.model.req.D_DayRequest;
import es.board.controller.model.req.ScheduleRequest;
import es.board.controller.model.req.SiteMeta;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.*;
import es.board.repository.document.JobSiteLog;
import es.board.repository.entity.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data

@Component
public class MainFunctionMapper {

    public List<InterviewAnswerDTO> fromAnswerList(List<InterviewAnswer> answer) {
        return answer.stream()
                .map(answer1 ->InterviewAnswerDTO.builder()
                        .id(answer1.getId())
                        .title(answer1.getTitle())
                        .answer(answer1.getAnswer())
                        .build())
                        .collect(Collectors.toList());
    }
    public  InterviewAnswer toInterviewEntity(InterviewAnswerDTO dto,String userId) {
        return InterviewAnswer.builder()
                .questionId(dto.getQuestionId())
                .userId(userId)
                .title(dto.getTitle())
                .answer(dto.getAnswer())
                .likes(dto.getLikes())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Todo TodoToEntity(String userId, TodoResponse todoResponse) {
        return Todo.builder()
                .userId(userId)
                .title(todoResponse.getTitle())
                .priority(todoResponse.getPriority())
                .category(todoResponse.getCategory())
                .status(TodoStatus.IN_PROGRESS)
                .project(todoResponse.getProject())
                .description(todoResponse.getDescription())
                .createdAt(LocalDateTime.now())
                .end(todoResponse.getEnd() != null ? todoResponse.getEnd() : LocalDate.now())
                .build();
    }

    public List<TodoRequest> EntityToTodo(List<Todo> todo) {
        return todo.stream()
                .map(todo1 -> TodoRequest.builder()
                        .todo_id(todo1.getTodo_id())
                        .title(todo1.getTitle())
                        .description(todo1.getDescription())
                        .category(todo1.getCategory())
                        .priority(todo1.getPriority())
                        .status(todo1.getStatus())
                        .end(todo1.getEnd())
                        .build())
                .collect(Collectors.toList());
    }


    public  List<ScheduleRequest> fromSchedule(List<Schedule> schedule) {
        return schedule.stream()
                .map(schedule1 -> ScheduleRequest.builder()
                        .scheduleId(schedule1.getScheduleId())
                        .userId(schedule1.getUserId())
                        .title(schedule1.getTitle())
                        .category(schedule1.getCategory())
                        .description(schedule1.getDescription())
                        .allDay(schedule1.getAllDay())
                        .location(schedule1.getLocation())
                        .startDatetime(schedule1.getStartDatetime())
                        .endDatetime(schedule1.getEndDatetime())
                        .createdAt(schedule1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public Schedule toScheduleEntity(String userId, ScheduleRequest scheduleDTO) {
        return Schedule.builder()
                .userId(userId)
                .title(scheduleDTO.getTitle())
                .startDatetime(LocalDateTime.now())
                .category(scheduleDTO.getCategory())
                .endDatetime(scheduleDTO.getEndDatetime())
                .allDay(scheduleDTO.getAllDay())
                .location(scheduleDTO.getLocation())
                .description(scheduleDTO.getDescription())
                .isRepeat(scheduleDTO.getIsRepeat())
                .repeatDays(scheduleDTO.getRepeatDays())
                .repeatStartDate(scheduleDTO.getRepeatStartDate())
                .repeatEndDate(scheduleDTO.getRepeatEndDate())
                .build();
    }


    public es.board.repository.document.Schedule toScheduleDocument(String userId, ScheduleRequest scheduleDTO, Long id) {
        return es.board.repository.document.Schedule.builder()
                .scheduleId(id)
                .userId(userId)
                .title(scheduleDTO.getTitle())
                .startDatetime(LocalDateTime.now())
                .category(scheduleDTO.getCategory())
                .endDatetime(scheduleDTO.getEndDatetime())
                .allDay(scheduleDTO.getAllDay())
                .location(scheduleDTO.getLocation())
                .description(scheduleDTO.getDescription())
                .build();
    }

    public List<es.board.repository.document.Schedule> toScheduleDocumentList(String userId, List<Schedule> schedules) {
        return schedules.stream()
                .map(schedule -> es.board.repository.document.Schedule.builder()
                        .scheduleId(schedule.getScheduleId())
                        .userId(userId)
                        .title(schedule.getTitle())
                        .startDatetime(schedule.getStartDatetime())
                        .endDatetime(schedule.getEndDatetime())
                        .location(schedule.getLocation())
                        .category(schedule.getCategory())
                        .description(schedule.getDescription())
                        .createdAt(schedule.getCreatedAt())
                        .isRepeat(schedule.getIsRepeat())
                        .repeatDays(schedule.getRepeatDays())
                        .repeatStartDate(schedule.getRepeatStartDate())
                        .repeatEndDate(schedule.getRepeatEndDate())
                        .build())
                .collect(Collectors.toList());
    }

    public D_DayRequest toD_DayDTO(D_Day examSchedule) {
        return D_DayRequest.builder()
                .id(examSchedule.getId())
                .userId(examSchedule.getUserId())
                .category(examSchedule.getCategory())
                .examName(examSchedule.getExamName())
                .examDate(examSchedule.getExamDate())
                .goal(examSchedule.getGoal())
                .progress(examSchedule.getProgress())
                .createdAt(examSchedule.getCreatedAt())
                .build();
    }

    public JobSiteLogDTO toJobSiteDocument(JobSiteLogDTO dto,String  userId) {
        return JobSiteLogDTO.builder()
                .userId(userId)
                .siteName(dto.getSiteName())
                .link(dto.getLink())
                .ip(dto.getIp())
                .clickedAt(LocalDateTime.now())
                .build();
    }


    public   D_Day toEntityD_Day(String userId, D_DayRequest dto) {
        return D_Day.builder()
                .id(dto.getId())
                .userId(userId)
                .category(dto.getCategory())
                .examName(dto.getExamName())
                .examDate(dto.getExamDate())
                .goal(dto.getGoal())
                .createdAt(LocalDateTime.now())
                .build();
    }
    public  List<D_DayRequest> fromD_DayEntityList(List<D_Day> dDayList) {
        return dDayList.stream()
                .map(dDay -> D_DayRequest.builder()
                        .id(dDay.getId())
                        .userId(dDay.getUserId())
                        .category(dDay.getCategory())
                        .examName(dDay.getExamName())
                        .examDate(dDay.getExamDate())
                        .goal(dDay.getGoal())
                        .progress(dDay.getProgress())
                        .dDay(calculateDDay(dDay.getExamDate()))
                        .createdAt(dDay.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public  List<JobSiteLogDTO> fromSiteList(List<JobSiteLog> jobSiteLogs) {

        Map<String, SiteMeta> siteMetaMap = getSiteMetaMap();
        return jobSiteLogs.stream()
                .map(log -> {
                    SiteMeta meta = siteMetaMap.get(log.getSiteName());
                    return JobSiteLogDTO.builder()
                            .siteName(log.getSiteName())
                            .link(meta != null ? meta.getLink() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }


    public  List<ScheduleRequest> fromScheduleDocument(List<es.board.repository.document.Schedule> schedule) {
        return schedule.stream()
                .map(schedule1 -> ScheduleRequest.builder()
                        .scheduleId(schedule1.getScheduleId())
                        .userId(schedule1.getUserId())
                        .title(schedule1.getTitle())
                        .category(schedule1.getCategory())
                        .description(schedule1.getDescription())
                        .location(schedule1.getLocation())
                        .startDatetime(schedule1.getStartDatetime())
                        .endDatetime(schedule1.getEndDatetime())
                        .build())
                .collect(Collectors.toList());
    }

//    public  Bookmark fromDailyEntity(DailyBookMark dailyBookMark) {
//        return Bookmark.builder()
//                .user(dailyBookMark.getUserId())
//                .questionId(dailyBookMark.getQuestionId())
//                .category(dailyBookMark.getCategory())
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
    private Schedule createSchedule(String userId, ScheduleRequest scheduleDTO, LocalDate currentDate) {
        return Schedule.builder()
                .userId(userId)
                .title(scheduleDTO.getTitle())
                .startDatetime(LocalDateTime.of(currentDate, scheduleDTO.getStartDatetime().toLocalTime()))
                .endDatetime(LocalDateTime.of(currentDate, scheduleDTO.getEndDatetime().toLocalTime()))
                .allDay(scheduleDTO.getAllDay())
                .location(scheduleDTO.getLocation())
                .category(scheduleDTO.getCategory())
                .description(scheduleDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .isRepeat(true)
                .repeatDays(scheduleDTO.getRepeatDays())
                .repeatStartDate(scheduleDTO.getRepeatStartDate())
                .repeatEndDate(scheduleDTO.getRepeatEndDate())
                .build();
    }


    public List<Schedule> generateRepeatSchedules(String userId, ScheduleRequest scheduleDTO) {
        if (scheduleDTO.getRepeatFrequency()!=null){

            return generateRepeatFrequencySchedules(userId,scheduleDTO);
        }

        List<Schedule> repeatSchedules = new ArrayList<>();

        LocalDate start = scheduleDTO.getRepeatStartDate().toLocalDate();
        LocalDate end = scheduleDTO.getRepeatEndDate().toLocalDate();
        Set<DayOfWeek> repeatDaysSet = convertToDayOfWeekSet(scheduleDTO.getRepeatDays());

        LocalDate currentDate = start;

        while (currentDate != null && !currentDate.isAfter(end)) {
            if (repeatDaysSet.contains(currentDate.getDayOfWeek())) {
                Schedule schedule = Schedule.builder()
                        .userId(userId)
                        .title(scheduleDTO.getTitle())
                        .startDatetime(LocalDateTime.of(currentDate, scheduleDTO.getStartDatetime().toLocalTime()))
                        .endDatetime(LocalDateTime.of(currentDate, scheduleDTO.getEndDatetime().toLocalTime()))
                        .allDay(scheduleDTO.getAllDay())
                        .location(scheduleDTO.getLocation())
                        .category(scheduleDTO.getCategory())
                        .description(scheduleDTO.getDescription())
                        .createdAt(LocalDateTime.now())
                        .isRepeat(true)
                        .repeatDays(scheduleDTO.getRepeatDays())
                        .repeatStartDate(scheduleDTO.getRepeatStartDate())
                        .repeatEndDate(scheduleDTO.getRepeatEndDate())
                        .build();
                repeatSchedules.add(schedule);
            }


            currentDate = getNextRepeatDate(currentDate, repeatDaysSet, end);

        }
        return repeatSchedules;
    }

    public long calculateDDay(LocalDate examDate) {
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(today, examDate);
    }
    private LocalDate getNextRepeatDate(LocalDate currentDate, Set<DayOfWeek> repeatDaysSet, LocalDate end) {
        for (int i = 1; i <= 7; i++) {
            LocalDate nextDate = currentDate.plusDays(i);
            if (nextDate.isAfter(end)) {
                return null;
            }
            if (repeatDaysSet.contains(nextDate.getDayOfWeek())) {
                return nextDate;
            }
        }
        return null;
    }


    private Set<DayOfWeek> convertToDayOfWeekSet(String repeatDays) {
        Set<DayOfWeek> daysSet = new HashSet<>();
        if (repeatDays != null && !repeatDays.isEmpty()) {
            String[] days = repeatDays.split(",");
            for (String day : days) {
                switch (day.trim()) {
                    case "월": daysSet.add(DayOfWeek.MONDAY); break;
                    case "화": daysSet.add(DayOfWeek.TUESDAY); break;
                    case "수": daysSet.add(DayOfWeek.WEDNESDAY); break;
                    case "목": daysSet.add(DayOfWeek.THURSDAY); break;
                    case "금": daysSet.add(DayOfWeek.FRIDAY); break;
                    case "토": daysSet.add(DayOfWeek.SATURDAY); break;
                    case "일": daysSet.add(DayOfWeek.SUNDAY); break;
                }
            }
        }
        return daysSet;
    }

    public List<Schedule> generateRepeatFrequencySchedules(String userId, ScheduleRequest scheduleDTO) {
        List<Schedule> repeatSchedules = new ArrayList<>();

        LocalDate start = scheduleDTO.getRepeatStartDate().toLocalDate();
        LocalDate end = scheduleDTO.getRepeatEndDate().toLocalDate();
        LocalDate currentDate = start;

        String frequency = scheduleDTO.getRepeatFrequency();
        int interval = scheduleDTO.getRepeatInterval();

        Set<DayOfWeek> repeatDaysSet = new HashSet<>();
        if ("weekly".equals(frequency)) {
            repeatDaysSet = convertToDayOfWeekSet(scheduleDTO.getRepeatDays());
        }

        while (currentDate != null && !currentDate.isAfter(end)) {
            if ("daily".equals(frequency)) {
                repeatSchedules.add(createSchedule(userId, scheduleDTO, currentDate));
                currentDate = currentDate.plusDays(interval);

            } else if ("weekly".equals(frequency)) {
                if (repeatDaysSet.contains(currentDate.getDayOfWeek())) {
                    repeatSchedules.add(createSchedule(userId, scheduleDTO, currentDate));
                }
                currentDate = currentDate.plusDays(1);

            } else if ("monthly".equals(frequency)) {
                repeatSchedules.add(createSchedule(userId, scheduleDTO, currentDate));
                currentDate = currentDate.plusMonths(interval);
            }
        }

        return repeatSchedules;
    }

    private Map<String, SiteMeta> getSiteMetaMap() {
        List<SiteMeta> jobSites = List.of(
                new SiteMeta(
                        "잡코리아",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/JobKorea_logo.svg/512px-JobKorea_logo.svg.png",
                        "채용 정보 및 연봉 정보 제공",
                        "https://www.jobkorea.co.kr",
                        List.of("general", "it")
                ),
                new SiteMeta(
                        "사람인",
                        "https://www.saraminimage.co.kr/common/logo.png",
                        "국내 대표 취업 사이트",
                        "https://www.saramin.co.kr",
                        List.of("general", "it")
                ),
                new SiteMeta(
                        "원티드",
                        "https://static.wanted.co.kr/images/logo_wanted.png",
                        "이력서 등록 후 AI 매칭 추천",
                        "https://www.wanted.co.kr",
                        List.of("it")
                ),
                new SiteMeta(
                        "로켓펀치",
                        "https://rocketpunch.com/static/img/meta_og.png",
                        "스타트업 및 IT 취업 정보",
                        "https://www.rocketpunch.com",
                        List.of("it")
                ),
                new SiteMeta(
                        "점핏",
                        "https://noticon-static.tammolo.com/dgggcrkxq/image/upload/v1574966246/noticon/ehxzhlsfemcaox0rgqgt.png",
                        "IT 개발자 전문 취업 플랫폼",
                        "https://www.jumpit.co.kr",
                        List.of("it")
                ),
                new SiteMeta(
                        "OKKY",
                        "https://okky.kr/static/logo/logo.png",
                        "IT 개발자 커뮤니티 및 채용 정보",
                        "https://okky.kr",
                        List.of("it")
                ),
                new SiteMeta(
                        "프로그래머스",
                        "https://programmers.co.kr/assets/img/meta/main-visual.jpg",
                        "코딩 테스트 & IT 채용 플랫폼",
                        "https://programmers.co.kr",
                        List.of("it")
                ),
                new SiteMeta(
                        "직행",
                        "https://jikhaeng.com/static/images/favicon/favicon-32x32.png",
                        "IT 개발자를 위한 커리어 성장 플랫폼",
                        "https://www.jikhaeng.com",
                        List.of("it")
                ),
                new SiteMeta(
                        "부트텐트",
                        "https://www.bootcamp.co.kr/favicon.ico",
                        "IT 부트캠프 & 취업 지원 서비스",
                        "https://www.bootcamp.co.kr",
                        List.of("it")
                ),
                new SiteMeta(
                        "잡플래닛",
                        "https://www.jobplanet.co.kr/favicon.ico",
                        "기업 리뷰 & 연봉 정보 제공",
                        "https://www.jobplanet.co.kr",
                        List.of("it", "general")
                ),
                new SiteMeta(
                        "블라인드",
                        "https://www.teamblind.com/favicon.ico",
                        "익명 직장인 커뮤니티",
                        "https://www.teamblind.com",
                        List.of("general")
                ),
                new SiteMeta(
                        "링커리어",
                        "https://careerly.co.kr/favicon.ico",
                        "대외활동과 인턴 공고가 많은 플랫폼",
                        "https://www.linkareer.com",
                        List.of("general", "intern")
                ),
                new SiteMeta(
                        "워크넷",
                        "https://www.work.go.kr/images/common/favicon.ico",
                        "정부에서 운영하는 종합 취업 지원 플랫폼",
                        "https://www.work.go.kr",
                        List.of("general")
                ),
                new SiteMeta(
                        "슈퍼루키",
                        "https://superookie.com/favicon.ico",
                        "인턴·신입·계약직 채용 공고가 많음. 직무 찾기 용이",
                        "https://www.superookie.com",
                        List.of("intern")
                ),
                new SiteMeta(
                        "자소설닷컴",
                        "https://www.jasoseol.com/favicon.ico",
                        "달력형 채용 공고 제공, 지원자 수 확인 가능",
                        "https://www.jasoseol.com",
                        List.of("intern")
                ),
                new SiteMeta(
                        "요즘것들",
                        "https://yozm.wishket.com/favicon.ico",
                        "청년·밀레니얼 세대를 위한 대외활동 & 공모전 정보",
                        "https://yozm.wishket.com",
                        List.of("activity")
                ),
                new SiteMeta(
                        "캠퍼스",
                        "https://www.campus.co.kr/favicon.ico",
                        "대학생 및 청년 대상 대외활동·인턴십·공모전 정보 제공",
                        "https://www.campus.co.kr",
                        List.of("activity")
                ),
                new SiteMeta(
                        "위비티",
                        "https://www.wevity.com/favicon.ico",
                        "스타트업·IT 업계 대외활동 중심 구직 정보 플랫폼",
                        "https://www.wevity.com",
                        List.of("activity")
                ),
                new SiteMeta(
                        "여기부터",
                        "https://www.yogiboot.com/favicon.ico",
                        "청년 창업가를 위한 지원 프로그램 및 창업 정보 제공",
                        "https://www.yogiboot.com",
                        List.of("activity")
                )
        );
        return jobSites.stream().collect(Collectors.toMap(SiteMeta::getName, Function.identity()));
    }

}
