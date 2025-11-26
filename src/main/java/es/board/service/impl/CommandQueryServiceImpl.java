package es.board.service.impl;

import es.board.controller.model.dto.stats.PostStatsDTO;
import es.board.repository.entity.repository.PostRepository;
import es.board.repository.entity.repository.infrastructure.feed.*;
import es.board.repository.entity.repository.infrastructure.poll.PollRepository;
import es.board.service.CommandQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandQueryServiceImpl implements CommandQueryService {

    private final PostQueryRepository postRepository;

    private final PollRepository pollRepository;

    private final LikeRepository likeRepository;

    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;
    @Override
    public List<PostStatsDTO> getPostStats(int page, int size) {
        int safePage = Math.max(1, page+1);
        int offset = (safePage - 1) * size;
        List<Integer> ids = postRepository.findPostIds(offset, size);

        List<PostStatsDTO> resultList = getPostStatsDTOS(ids);

        return resultList;
    }

    @Override
    public List<PostStatsDTO> getPollStats(int page, int size) {
        int safePage = Math.max(1, page+1);
        int offset = (safePage - 1) * size;
        List<Integer> ids = pollRepository.findPollIds(offset,size);
        List<PostStatsDTO> resultList = getPostStatsDTOS(ids);

        return resultList;
    }
    @Override
    public List<PostStatsDTO> getBestWeekPostStats(int page, int size) {
        int safePage = Math.max(1, page+1);
        int offset = (safePage - 1) * size;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSevenDay = now.minusDays(7);
        List<Integer> ids = postRepository.findBestWeekPostIds(offset, size,lastSevenDay);

        List<PostStatsDTO> resultList = getPostStatsDTOS(ids);

        return resultList;
    }

    @NotNull
    private List<PostStatsDTO> getPostStatsDTOS(List<Integer> ids) {
        Map<Integer, Long> comments = commentRepository.countCommentsIn(ids).stream()
                .collect(Collectors.toMap(CommentAggView::getPostId, CommentAggView::getCnt));

        Map<Integer, Long> replies = replyRepository.countRepliesIn(ids).stream()
                .collect(Collectors.toMap(ReplyAggView::getPostId, ReplyAggView::getCnt));

        Map<Integer, Long> combinedComments = new HashMap<>(comments);
        replies.forEach((postId, replyCount) -> {
            combinedComments.merge(postId, replyCount, (existingCommentCount, newReplyCount) ->
                    existingCommentCount + newReplyCount);
        });
        Map<Integer, Long> likes = likeRepository.findPagingLikes(ids).stream()
                .collect(Collectors.toMap(LikeAggView::getPostId, LikeAggView::getCnt));

        List<PostStatsDTO> resultList = ids.stream()
                .map(postId -> {
                    // 합쳐진 댓글/답글 수 가져오기 (없으면 0L)
                    Long totalComments = combinedComments.getOrDefault(postId, 0L);
                    // 좋아요 수 가져오기 (없으면 0L)
                    Long likeCount = likes.getOrDefault(postId, 0L);

                    return new PostStatsDTO(postId, totalComments, likeCount);
                })
                .collect(Collectors.toList());
        return resultList;
    }

}
