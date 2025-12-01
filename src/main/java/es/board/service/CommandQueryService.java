package es.board.service;

import es.board.controller.model.dto.stats.PostStatsDTO;

import java.util.List;
import java.util.Map;

public interface CommandQueryService {

    List<PostStatsDTO> getBestWeekPostStats(int page, int size);

    List<PostStatsDTO> getPostStats(int page, int size);

    List<PostStatsDTO> getPollStats(int page,int size);

}
