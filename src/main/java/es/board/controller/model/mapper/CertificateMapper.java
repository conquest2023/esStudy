package es.board.controller.model.mapper;


import es.board.controller.model.req.QuestionPracticalDto;
import es.board.controller.model.req.ScheduleRequest;
import es.board.controller.model.req.TodoRequest;
import es.board.controller.model.res.TodoResponse;
import es.board.repository.entity.QuestionPractical;
import es.board.repository.entity.Schedule;
import es.board.repository.entity.Todo;
import es.board.repository.entity.TodoStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Slf4j
@Component
public class CertificateMapper {

        public List<QuestionPracticalDto> fromPracticalDTO(List<QuestionPractical> practicals) {
            return practicals.stream()
                    .map(practical -> QuestionPracticalDto.builder()
                            .questionId(practical.getQuestionId())
                            .category(practical.getCategory())
                            .type(practical.getType())
                            .explanation(practical.getExplanation())
                            .modelAnswer(practical.getModelAnswer())
                            .questionText(practical.getQuestionText())
                            .build())
                    .collect(Collectors.toList());
    }

//    public QuestionPracticalDto ToQuestionPracticalEntity() {
//        return QuestionPractical.builder()
//                .questionId()
//                .title(todoResponse.getTitle())
//                .priority(todoResponse.getPriority())
//                .category(todoResponse.getCategory())
//                .status(TodoStatus.IN_PROGRESS)
//                .description(todoResponse.getDescription())
//                .dueDate(todoResponse.getDueDate())
//                .createdAt(LocalDateTime.now())
//                .build();
//    }



    public Todo TodoToEntity(String userId, TodoResponse todoResponse) {
        return Todo.builder()
                .userId(userId)
                .title(todoResponse.getTitle())
                .priority(todoResponse.getPriority())
                .category(todoResponse.getCategory())
                .status(TodoStatus.IN_PROGRESS)
                .description(todoResponse.getDescription())
                .dueDate(todoResponse.getDueDate())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<TodoRequest> EntityToTodo(List<Todo> todo) {
        return todo.stream()
                .map(todo1 -> TodoRequest.builder()
                        .todo_id(todo1.getTodo_id())
//                        .userId(todo1.getUserId())
                        .title(todo1.getTitle())
                        .description(todo1.getDescription())
                        .category(todo1.getCategory())
                        .priority(todo1.getPriority())
                        .status(todo1.getStatus())
                        .dueDate(todo1.getDueDate())
                        .createdAt(todo1.getCreatedAt())
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

    // DTO -> 엔터티 변환 메서드
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
                        .scheduleId(schedule.getScheduleId()) // MySQL에서 저장된 ID
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

    public List<Schedule> generateRepeatSchedules(String userId, ScheduleRequest scheduleDTO) {
        List<Schedule> repeatSchedules = new ArrayList<>();

        LocalDate start = scheduleDTO.getRepeatStartDate().toLocalDate();
        LocalDate end = scheduleDTO.getRepeatEndDate().toLocalDate(); //  전체 반복 종료일 사용
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

            // ✅ 다음 반복 요일로 점프
            currentDate = getNextRepeatDate(currentDate, repeatDaysSet, end);

        }
        return repeatSchedules;
    }


    private LocalDate getNextRepeatDate(LocalDate currentDate, Set<DayOfWeek> repeatDaysSet, LocalDate end) {
        // 🔹 다음 가능한 반복 요일 찾기
        for (int i = 1; i <= 7; i++) {
            LocalDate nextDate = currentDate.plusDays(i);
            if (nextDate.isAfter(end)) {
                return null; // 종료일을 넘어가면 반복 중지
            }
            if (repeatDaysSet.contains(nextDate.getDayOfWeek())) {
                return nextDate;
            }
        }
        return null; // 반복 요일이 없으면 종료
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
}
