package es.board.service.impl;

import es.board.controller.model.dto.stats.PostStatsDTO;
import es.board.repository.entity.repository.PostRepository;
import es.board.repository.entity.repository.infrastructure.feed.*;
import es.board.service.CommandQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandQueryServiceImpl implements CommandQueryService {

    private final PostQueryRepository postRepository;

    private final LikeRepository likeRepository;

    private final CommentRepository commentRepository;

    private final ReplyRepository replyRepository;
    @Override
    public List<PostStatsDTO> getPostStats(int page, int size) {
        List<Integer> ids = postRepository.findPostIds(page, size);

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
