package es.board.controller.model.mapper;

import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.NoticeDTO;
import es.board.controller.model.req.VoteResponse;
import es.board.controller.model.res.LikeResponse;
import es.board.repository.document.Board;
import es.board.repository.entity.Likes;
import es.board.repository.entity.Notice;
import es.board.repository.entity.UserVote;
import es.board.repository.entity.Vote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Component
@Slf4j
public class FeedMapper {

    public List<FeedRequest> BoardListToDTO(List<Board> boards) {
        return boards.stream()
                .map(board -> FeedRequest.builder()
                        .feedUID(board.getFeedUID())
                        .id(board.getId())
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
                .created_at(LocalDateTime.now())
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
                .imageURL(notice.getImageURL())
                .description(notice.getDescription())
                .username(notice.getUsername())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    public  Notice ToNotice(NoticeDTO notice,String userId) {
        return Notice.builder()
                .id(notice.getId())
                .feedUID(notice.getFeedUID())
                .userId(userId)
                .title(notice.getTitle())
                .description(notice.getDescription())
                .imageURL(notice.getImageURL())
                .username("관리자")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Vote voteToEntity(VoteResponse voteResponse,String  username, String userId) {
        return Vote.builder()
                .userId(userId)
                .username(username)
                .title(voteResponse.getTitle())
                .description(voteResponse.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VoteResponse voteToDTO(Vote vote ,String username, String userId) {
        return VoteResponse.builder()
                .userId(userId)
                .username(username)
                .title(vote.getTitle())
                .description(vote.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VoteResponse userVoteToDTO(UserVote vote , String username, String userId) {
        return VoteResponse.builder()
                .id(vote.getId())
                .voteId(vote.getVoteId())
                .userId(userId)
                .username(username)
                .upvote(vote.isUpvote())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VoteResponse voteToDTOMain(Vote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .username(vote.getUsername())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .createdAt(vote.getCreatedAt())
                .build();
    }


    public List<VoteResponse> voteToDTOMainList(List<Vote> vote) {
        return vote.stream()
                .map(vote1 -> VoteResponse.builder()
                        .id(vote1.getId())
                        .username(vote1.getUsername())
                        .title(vote1.getTitle())
                        .description(vote1.getDescription())
                        .createdAt(vote1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public  UserVote userVoteToEntity(VoteResponse voteResponse,String  username, String userId) {
        return UserVote.builder()
                .voteId(voteResponse.getVoteId())
                .userId(userId)
                .upvote(voteResponse.getUpvote())
                .username(username)
                .createdAt(LocalDateTime.now())
                .build();
    }

}
