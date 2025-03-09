package es.board.service.impl;

import es.board.controller.model.mapper.CommentMapper;
import es.board.controller.model.mapper.FeedMapper;
import es.board.controller.model.req.CommentRequest;
import es.board.controller.model.req.CommentUpdate;
import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.res.CommentCreate;
import es.board.repository.CommentDAO;
import es.board.repository.LikeDAO;
import es.board.repository.document.Comment;
import es.board.repository.entity.entityrepository.PostRepository;
import es.board.service.CommentService;
import es.board.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final CommentDAO commentDAO;

    private  final LikeDAO likeDAO;

    private  final CommentMapper commentMapper;

    private  final FeedMapper feedMapper;

    private  final PostRepository postRepository;



    private  final NotificationService notificationService;

    @Override
    public Map<String, Object> getUserComments(String userId) {

        return commentDAO.findUserComments(userId);
    }

    @Override
    public List<CommentRequest> getUserRangeTimeActive(String userId) {

      return  commentMapper.changeCommentListDTO(commentDAO.findUserRangeActive(userId));
    }

    @Override
    public List<FeedRequest> getFeedAndCommentMyPage(String userId,int page ,int size) {

        return  feedMapper.BoardListToDTO(commentDAO.findFeedAndCommentMypage(userId,page,size));
    }

    @Override
     public List<Comment> getMyPageComment(String  userId, int num ,int size){
        return commentDAO.findMyPagePagingComment(userId,num,size);
    }
    @Override
    public String saveDocument(String indexName, CommentCreate dto) {


        return commentDAO.createCommentOne(indexName, dto);
    }

    @Override
    public List<Comment> getSearchComment(String text) {
        return commentDAO.findSearchComment(text);
    }

    @Override
    public double getUserCommentCount(String userId) {
        return commentDAO.findUserCommentCount(userId);
    }

    @Override
    public Comment editComment(String id, CommentUpdate eq) {
        Comment comment = new Comment();
        return commentDAO.modifyComment(id, comment.convertDtoToEntity(eq));
    }

    @Override
    public List<CommentRequest> getRecentComment() {

        return commentMapper.changeCommentListDTO(commentDAO.findRecentComment());
    }

    @Override
    public void indexComment(CommentCreate dto) {
        checkValueComment(dto);
        String userId = postRepository.findByFeedUID(dto.getFeedUID());
        commentDAO.saveCommentIndex(dto);
        if (userId == null) {
            log.info("공지사항에 댓글 작성됨: {}", dto.getFeedUID());
        } else {
            if (!userId.equals(dto.getUserId())) {
                notificationService.sendCommentNotification(userId, dto.getFeedUID(),
                        dto.getUsername() + "님이 댓글을 작성하였습니다: " + dto.getContent());
            }
        }
    }



    @Override
    public void plusCommentLike(String id) {
            likeDAO.saveCommentLike(id);
    }

    @Override
    public List<CommentCreate> createBulkComment(List<CommentCreate> comments) {
        commentDAO.CreateManyComment(BulkToEntity(comments));
        return comments;
    }

    @Override
    public  Map<String, Object> findCommentsWithCount(String feedUID){
        return commentDAO.findCommentsWithCount(feedUID);
    }

    @Override
    public List<CommentRequest> getLikeCount() {

        return commentMapper.changeCommentListDTO(commentDAO.findLikeCount());
    }

    @Override
    public Map<String, Double> getPagingComment(List<String> feedUIDs, int num, int size) {
        return commentDAO.findPagingComment(feedUIDs, num, size);
    }

    @Override
    public Map<String, Double> getPagingCommentDESC(List<String> feedUIDs, int num, int size) {
        return commentDAO.findPagingCommentDESC(feedUIDs, num, size);
    }

    @Override
    public List<CommentRequest> getComment() {

        return commentMapper.changeCommentListDTO(commentDAO.findCommentAll());
    }

    @Override
    public List<CommentRequest> getCommentOne(String commentUID){

        return  commentMapper.changeCommentListDTO(commentDAO.findCommentId(commentUID));
    }

    @Override
    public List<Comment> getCommentId(String id) {

        return commentDAO.findDetailComment(id);
    }

    @Override
    public void deleteComment(String id) {
        commentDAO.deleteCommentId(id);
    }


    public List<Comment> BulkToEntity(List<CommentCreate> res) {
        List<Comment> comments = new ArrayList<>();
        for (CommentCreate dto : res) {
            Comment comment = Comment.builder()
                    .commentUID(dto.getCommentUID())
                    .username(dto.getUsername())
                    .content(dto.getContent())
                    .createdAt(dto.getCreatedAt())
                    .build();
            comments.add(comment);
        }
        return comments;
    }

    private static void checkValueComment(CommentCreate commentCreate) {

        if (isEmpty(commentCreate.getUsername()) || isEmpty(commentCreate.getContent())) {
            throw new IllegalArgumentException("내용은 필수 입력값입니다.");
        }
    }
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
