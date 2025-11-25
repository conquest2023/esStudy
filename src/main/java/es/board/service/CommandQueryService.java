package es.board.service;

import es.board.controller.model.dto.stats.PostStatsDTO;

import java.util.List;
import java.util.Map;

public interface CommandQueryService {



    List<PostStatsDTO> getPostStats(int page, int size);
}
