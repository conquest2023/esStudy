package es.board.service.impl;

import es.board.controller.model.dto.todo.D_DayDTO;
import es.board.repository.entity.D_Day;
import es.board.repository.entity.repository.D_DayRepository;
import es.board.service.DDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DDayServiceImpl implements DDayService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final D_DayRepository dDayRepository;

    @Override
    public List<D_DayDTO> getUpcoming(String userId) {
        LocalDate today = LocalDate.now(KST);
        return dDayRepository.findAll(userId).stream()
                .filter(d -> !d.getExamDate().isBefore(today))
                .sorted(Comparator.comparing(D_Day::getExamDate))
                .map(d -> toDto(d, today))
                .toList();
    }

    @Override
    @Transactional
    public D_DayDTO save(String userId, D_DayDTO request) {
        if (request.getExamName() == null || request.getExamName().isBlank() || request.getExamDate() == null) {
            throw new IllegalArgumentException("시험 이름과 날짜는 필수입니다.");
        }
        D_Day saved = dDayRepository.save(D_Day.builder()
                .userId(userId)
                .category(request.getCategory() == null || request.getCategory().isBlank() ? "기타" : request.getCategory())
                .examName(request.getExamName().trim())
                .examDate(request.getExamDate())
                .goal(request.getGoal())
                .progress(request.getProgress() == null ? 0 : request.getProgress())
                .createdAt(LocalDateTime.now(KST))
                .build());
        return toDto(saved, LocalDate.now(KST));
    }

    @Override
    @Transactional
    public void delete(String userId, Long id) {
        dDayRepository.findById(id)
                .filter(d -> d.getUserId().equals(userId))
                .ifPresent(dDayRepository::delete);
    }

    private static D_DayDTO toDto(D_Day d, LocalDate today) {
        return D_DayDTO.builder()
                .id(d.getId())
                .userId(d.getUserId())
                .category(d.getCategory())
                .examName(d.getExamName())
                .examDate(d.getExamDate())
                .goal(d.getGoal())
                .progress(d.getProgress())
                .dDay(ChronoUnit.DAYS.between(today, d.getExamDate()))
                .createdAt(d.getCreatedAt())
                .build();
    }
}
