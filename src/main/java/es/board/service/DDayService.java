package es.board.service;

import es.board.controller.model.dto.todo.D_DayDTO;

import java.util.List;

public interface DDayService {

    /** 오늘 이후(오늘 포함) D-Day, 가까운 날짜 순 */
    List<D_DayDTO> getUpcoming(String userId);

    D_DayDTO save(String userId, D_DayDTO request);

    void delete(String userId, Long id);
}
