package es.board.service.impl;

import es.board.controller.model.dto.stats.PostStatsDTO;
import es.board.infrastructure.feed.CommentAggView;
import es.board.infrastructure.feed.LikeAggView;
import es.board.infrastructure.feed.PostQueryRepository;
import es.board.infrastructure.feed.ReplyAggView;
import es.board.infrastructure.poll.PollRepository;
import es.board.service.CommandQueryService;
import es.board.domain.CommentRepository;
import es.board.domain.LikeRepository;
import es.board.domain.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        int safePage = Math.max(1, page + 1);
        int offset = (safePage - 1) * size;
        List<Integer> ids = postRepository.findPostIds(offset, size);

        List<PostStatsDTO> resultList = getPostStatsDTOS(ids);

        return resultList;
    }

    @Override
    public List<PostStatsDTO> getPollStats(int page, int size) {
        int safePage = Math.max(1, page + 1);
        int offset = (safePage - 1) * size;
        List<Integer> ids = pollRepository.findPollIds(offset, size);
        List<PostStatsDTO> resultList = getPostStatsDTOS(ids);

        return resultList;
    }

    @Override
    public List<PostStatsDTO> getBestWeekPostStats(int page, int size) {
        int safePage = Math.max(1, page + 1);
        int offset = (safePage - 1) * size;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastSevenDay = now.minusDays(7);
        List<Integer> ids = postRepository.findBestWeekPostIds(offset, size, lastSevenDay);

        List<PostStatsDTO> resultList = getPostStatsDTOS(ids);

        return resultList;
    }

    @NotNull
    private List<PostStatsDTO> getPostStatsDTOS(List<Integer> ids) {
        Map<Integer, Long> comments = commentRepository.countCommentsIn(ids).stream()
                .collect(Collectors.toMap(CommentAggView::getPostId, CommentAggView::getCnt));

        Map<Integer, Long> replies = replyRepository.countRepliesIn(ids).stream()
                .collect(Collectors.toMap(ReplyAggView::getPostId, ReplyAggView::getCnt));

        Map<Integer, Long> likes = likeRepository.findPagingLikes(ids).stream()
                .collect(Collectors.toMap(LikeAggView::getPostId, LikeAggView::getCnt));


        Map<Integer, Long> combinedComments = new HashMap<>(comments);
        replies.forEach((postId, replyCount) -> {
            combinedComments.merge(postId, replyCount, (existingCommentCount, newReplyCount) ->
                    existingCommentCount + newReplyCount);
        });
        List<PostStatsDTO> resultList = ids.stream()
                .map(postId -> {
                    Long totalComments = combinedComments.getOrDefault(postId, 0L);
                    Long likeCount = likes.getOrDefault(postId, 0L);
                    return new PostStatsDTO(postId, totalComments, likeCount);
                })
                .collect(Collectors.toList());
        return resultList;
    }
}
