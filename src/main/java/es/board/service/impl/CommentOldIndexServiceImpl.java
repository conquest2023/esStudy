//package es.board.service.impl;
//
//import es.board.controller.model.dto.feed.PostDTO;
//import es.board.controller.model.mapper.CommentMapper;
//import es.board.controller.model.mapper.document.FeedDocumentMapper;
//import es.board.controller.model.dto.feed.CommentDTO;
//import es.board.repository.CommentDAO;
//import es.board.repository.LikeDAO;
//import es.board.repository.ReplyDAO;
//import es.board.infrastructure.es.document.Feed;
//import es.board.repository.document.Comment;
//import es.board.repository.entity.repository.PostRepository;
//import es.board.service.CommentIndexService;
//import es.board.infrastructure.mq.old.FeedEvent;
//import es.board.infrastructure.mq.producer.CommentOldEventPublisher;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class CommentIndexServiceImpl implements CommentIndexService {
//
//
//
//    private  final FeedDocumentMapper feedMapper;
//
////    private  final SlackNotifier slackNotifier;
//
//    private  final CommentMapper commentMapper;
//
//
////    private  final NotificationService notificationService;
//
//    private  final CommentOldEventPublisher commentEventPublisher;
//
//    private final CommentDAO commentDAO;
//
//    private  final ReplyDAO replyDAO;
//
//    private  final LikeDAO likeDAO;
//
//    private  final PostRepository postRepository;
//
//
//
//
//    @Override
//    public Map<String, Object> getUserComments(String userId) {
//
//        return commentDAO.findUserComments(userId);
//    }
//
//    @Override
//    public List<Feed> getMantComment() {
//
//        return commentDAO.findManyComment();
//    }
//    @Override
//    public List<CommentDTO.Request> getUserRangeTimeActive(String userId) {
//
//      return  commentMapper.changeCommentListDTO(commentDAO.findUserRangeActive(userId));
//    }
//
//    @Override
//    public List<PostDTO.Request> getFeedAndCommentMyPage(String userId, int page , int size) {
//
//        return  feedMapper.fromBoardDtoList(commentDAO.findFeedAndCommentMypage(userId,page,size));
//    }
//
//    @Override
//     public List<Comment> getMyPageComment(String  userId, int num ,int size){
//        return commentDAO.findMyPagePagingComment(userId,num,size);
//    }
//    @Override
//    public List<Comment> getSearchComment(String text) {
//        return commentDAO.findSearchComment(text);
//    }
//
//
//    @Override
//    public Comment editComment(String id, CommentDTO.Update eq) {
//        return commentDAO.modifyComment(id, commentMapper.convertDtoToEntity(eq));
//    }
//
//    @Override
//    public List<CommentDTO.Request> getRecentComment() {
//
//        return commentMapper.changeCommentListDTO(commentDAO.findRecentComment());
//    }
//
//    @Override
//    public void saveComment(CommentDTO.Response dto) {
//        checkValueComment(dto);
//        commentDAO.saveCommentIndex(dto);
//
//
//        FeedEvent event = FeedEvent.builder()
////                .feedUID(dto.getFeedUID())
////                .postOwnerId(userId)
////                .commentUID(dto.getCommentUID())
//                .commenterId(dto.getUserId())
//                .username(dto.getUsername())
//                .content(dto.getContent())
//                .createdAt(dto.getCreatedAt())
//                .build();
//
//        commentEventPublisher.publishCommentEvent(event);
//
////        slackNotifier.sendMessage(String.format("%s님이 \"%s\" 글을 작성하셨습니다",
////                dto.getUsername(),
////                dto.getContent().replace("\"", "'")));
//
////        if (userId!= null && !userId.equals(dto.getUserId())) {
////            notificationService.sendCommentNotification(userId, dto.getFeedUID(),
////                    dto.getUsername() + "님이 댓글을 작성하였습니다: " + dto.getContent());
////            notificationRepository.save(commentMapper.toCommentNotification(userId,dto));
////        }
////        grantCommentPoint(dto.getUserId(),dto.getUsername());
//    }
//
//
//
//    @Override
//    public void plusCommentLike(String id) {
//            likeDAO.saveCommentLike(id);
//    }
//
//    @Override
//    public List<CommentDTO.Response> saveBulkComment(List<CommentDTO.Response> comments) {
//        commentDAO.CreateManyComment(BulkToEntity(comments));
//        return comments;
//    }
//
//    @Override
//    public  Map<String, Object> findCommentsWithCount(String feedUID){
//        return commentDAO.findCommentsWithCount(feedUID);
//    }
//    @Override
//    public List<CommentDTO.Request> getLikeCount() {
//
//        return commentMapper.changeCommentListDTO(commentDAO.findLikeCount());
//    }
//
//    @Override
//    public Map<String, Double> getCommentAndReplyAggregation(List<String> feedUIDs, int num, int size) {
//        return getStringDoubleMap(feedUIDs, replyDAO.findAggregationReply(feedUIDs),
//                commentDAO.findPagingComment(feedUIDs, num, size));
//    }
//    @Override
//    public List<Comment> getMostCommentCount() {
//        return commentDAO.findMostCommentCount();
//    }
//
//
//    @Override
//    public List<CommentDTO.Request> getCommentOne(String commentUID){
//
//        return  commentMapper.changeCommentListDTO(commentDAO.findCommentId(commentUID));
//    }
//
//    @Override
//    public Map<String,Long> getTodayCommentAggregation(){
//            return commentDAO.findTodayCommentAggregation();
//    }
//
//
//    @Override
//    public void deleteComment(String id) {
//        commentDAO.deleteCommentId(id);
//    }
//
//
//
//
//    public List<Comment> BulkToEntity(List<CommentDTO.Response> res) {
//        List<Comment> comments = new ArrayList<>();
//        for (CommentDTO.Response dto : res) {
//            Comment comment = Comment.builder()
////                    .commentUID(dto.getCommentUID())
//                    .username(dto.getUsername())
//                    .content(dto.getContent())
//                    .createdAt(dto.getCreatedAt())
//                    .build();
//            comments.add(comment);
//        }
//        return comments;
//    }
//
//    private static void checkValueComment(CommentDTO.Response commentCreate) {
//        if (isEmpty(commentCreate.getContent())) {
//            throw new IllegalArgumentException("내용은 필수 입력값입니다.");
//        }
//    }
//    private static boolean isEmpty(String value) {
//
//        return value == null || value.trim().isEmpty();
//    }
//
//    private static Map<String, Double> getStringDoubleMap(List<String> feedUIDs, Map<String, Double> replyCounts, Map<String, Double> commentCounts) {
//        Map<String, Double> aggregatedData = new HashMap<>();
//        for (String feedUID : feedUIDs) {
//            double replyCount = replyCounts.getOrDefault(feedUID, 0.0);
//            double commentCount = commentCounts.getOrDefault(feedUID, 0.0);
//            aggregatedData.put(feedUID, replyCount + commentCount);
//        }
//        return aggregatedData;
//    }
//
//}
