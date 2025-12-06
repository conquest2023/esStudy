package es.board.service;

import es.board.controller.model.dto.stats.PostStatsDTO;
import es.board.infrastructure.entity.feed.PostEntity;
import es.board.infrastructure.jpa.projection.PostWithCommentCount;
import es.board.infrastructure.jpa.projection.PostWithLikeCount;
import es.board.infrastructure.jpa.projection.PostWithReplyCount;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommandQueryService {

    List<PostStatsDTO> getBestPostStats(String day, int page, int size);

    List<PostStatsDTO> getPostStatsByIds(List<Integer> postIds);
    Page<PostWithReplyCount> getPostWithReplyCount(int page, int size);

    Page<PostWithLikeCount> findByPostWithLikeCount(int page , int size);

    Page<PostWithCommentCount> getPostWithCommentCount(int page,int size);

    Page<PostEntity> getPostViewCount(int page , int size);
    List<PostStatsDTO> getPostStats(int page, int size);

    List<PostStatsDTO> getPollStats(int page,int size);

}
