package es.board.controller.model.mapper;


import es.board.controller.model.req.D_DayDTO;
import es.board.controller.model.req.ScheduleDTO;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.InterviewAnswerDTO;
import es.board.controller.model.res.TodoResponse;
import es.board.repository.entity.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Slf4j
@Component
public class MainFunctionMapper {

    public InterviewAnswerDTO fromInterviewDTO(InterviewAnswer answer) {
        return InterviewAnswerDTO.builder()
                .id(answer.getId())
                .questionId(answer.getQuestionId())
                .userId(answer.getUserId())
                .answer(answer.getAnswer())
                .likes(answer.getLikes())
                .createdAt(answer.getCreatedAt())
                .build();
    }

    public  InterviewAnswer toInterviewEntity(InterviewAnswerDTO dto,String userId) {
        return InterviewAnswer.builder()
                .questionId(dto.getQuestionId())
                .userId(userId)
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


    public  List<ScheduleDTO> fromSchedule(List<Schedule> schedule) {
        return schedule.stream()
                .map(schedule1 -> ScheduleDTO.builder()
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

    // DTO -> 엔터티 변환 메서드
    public Schedule toScheduleEntity(String userId, ScheduleDTO scheduleDTO) {
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


    public es.board.repository.document.Schedule toScheduleDocument(String userId, ScheduleDTO scheduleDTO,Long id) {
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

    public D_DayDTO toD_DayDTO(D_Day examSchedule) {
        return D_DayDTO.builder()
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


    public   D_Day toEntityD_Day(String userId, D_DayDTO dto) {
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
    public  List<D_DayDTO> fromD_DayEntityList(List<D_Day> dDayList) {
        return dDayList.stream()
                .map(dDay -> D_DayDTO.builder()
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


    public  List<ScheduleDTO> fromScheduleDocument(List<es.board.repository.document.Schedule> schedule) {
        return schedule.stream()
                .map(schedule1 -> ScheduleDTO.builder()
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


    public List<Schedule> generateRepeatSchedules(String userId, ScheduleDTO scheduleDTO) {
        if (scheduleDTO.getRepeatFrequency()!=null){
            log.info("test1={}",scheduleDTO.toString());

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

    public List<Schedule> generateRepeatFrequencySchedules(String userId, ScheduleDTO scheduleDTO) {
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
    private Schedule createSchedule(String userId, ScheduleDTO scheduleDTO, LocalDate currentDate) {
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
}
