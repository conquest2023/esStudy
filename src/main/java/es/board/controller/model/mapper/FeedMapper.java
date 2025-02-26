package es.board.controller.model.mapper;

import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.NoticeDTO;
import es.board.controller.model.res.LikeResponse;
import es.board.repository.document.Board;
import es.board.repository.entity.Likes;
import es.board.repository.entity.Notice;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class FeedMapper {

    public List<FeedRequest> BoardListToDTO(List<Board> boards) {
        return boards.stream()
                .map(board -> FeedRequest.builder()
                        .feedUID(board.getFeedUID())
                        .id(board.getId())
//                        .userId(board.getUserId())
                        .username(board.getUsername())
                        .imageURL(board.getImageURL())
                        .title(board.getTitle())
                        .description(board.getDescription())
                        .likeCount(board.getLikeCount())
                        .category(board.getCategory())
                        .viewCount(board.getViewCount())
                        .createdAt(board.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    public FeedRequest isAuthorList(Board board, Boolean isAuthor) {
        return      FeedRequest.builder()
                        .feedUID(board.getFeedUID())
                        .id(board.getId())
                        .userId(board.getUserId())
                        .username(board.getUsername())
                        .imageURL(board.getImageURL())
                        .title(board.getTitle())
                        .description(board.getDescription())
                        .likeCount(board.getLikeCount())
                        .category(board.getCategory())
                        .viewCount(board.getViewCount())
                        .isAuthor(isAuthor)
                        .createdAt(board.getCreatedAt())
                        .build();
    }


    public FeedRequest BoardToDTO(Board board) {
        return FeedRequest.builder()
                .feedUID(board.getFeedUID())
                .id(board.getId())
                .userId(board.getUserId())
                .username(board.getUsername())
                .imageURL(board.getImageURL())
                .title(board.getTitle())
                .description(board.getDescription())
                .category(board.getCategory())
                .viewCount(board.getViewCount())
                .likeCount(board.getLikeCount())
                .createdAt(board.getCreatedAt())
                .build();
    }


    public Likes LikeToEntity(String feedUID, String userId) {
        return Likes.builder()
                .feedUID(feedUID)
                .userId(userId)
                .created_at(LocalDateTime.now()) // 현재 시간 저장
                .build();
    }

    public  List<NoticeDTO> fromNoticeList(List<Notice> notice) {
        return notice.stream()
                .map(notice1 -> NoticeDTO.builder()
                        .id(notice1.getId())
                        .feedUID(notice1.getFeedUID())
                        .username(notice1.getUsername())
                        .title(notice1.getTitle())
                        .description(notice1.getDescription())
                        .createdAt(notice1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public  NoticeDTO fromNotice(Notice notice) {
        return NoticeDTO.builder()
                .id(notice.getId())
                .userId(notice.getUserId())
                .feedUID(notice.getFeedUID())
                .title(notice.getTitle())
                .description(notice.getDescription())
                .username(notice.getUsername())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    public  Notice ToNotice(NoticeDTO notice,String userId) {
        return Notice.builder()
                .id(notice.getId())
                .userId(userId)
                .title(notice.getTitle())
                .description(notice.getDescription())
                .username("관리자")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
