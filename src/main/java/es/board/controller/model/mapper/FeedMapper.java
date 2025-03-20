package es.board.controller.model.mapper;

import es.board.controller.model.req.FeedRequest;
import es.board.controller.model.req.NoticeDTO;
import es.board.controller.model.req.ReplyRequest;
import es.board.controller.model.req.VoteDTO;
import es.board.controller.model.res.FeedCreateResponse;
import es.board.repository.document.Board;
import es.board.repository.document.Reply;
import es.board.repository.document.VoteDocument;
import es.board.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Component
@Slf4j
public class FeedMapper {
    public Post PostToEntity(FeedCreateResponse feedSaveDTO) {
        return Post.builder()
                .userId(feedSaveDTO.getUserId())
                .feedUID(UUID.randomUUID().toString())
                .username(feedSaveDTO.getUsername())
                .title(feedSaveDTO.getTitle())
                .imageUrl(feedSaveDTO.getImageURL())
                .anonymous(feedSaveDTO.isAnonymous())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Board BoardToDocument(FeedCreateResponse response, int id ,String feedUID) {
        log.info(response.toString());
        return Board.builder()
                .feedUID(feedUID)
                .id(id)
                .userId(response.getUserId())
                .username(response.getUsername())
                .imageURL(response.getImageURL())
                .title(response.getTitle())
                .description(response.getDescription().replace("\\n", "\n"))
                .category(response.getCategory())
                .viewCount(response.getViewCount())
                .likeCount(response.getLikeCount())
                .createdAt(LocalDateTime.now())
                .build();
    }

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

    public FeedRequest BoardToDTO(Board board) {
        return FeedRequest.builder()
                .feedUID(board.getFeedUID())
                .id(board.getId())
                .userId(board.getUserId())
                .username(board.getUsername())
                .imageURL(board.getImageURL())
                .title(board.getTitle())
                .description(board.getDescription().replace("\n", "<br>"))
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

    public Vote voteToEntity(VoteDTO voteResponse, String  username, String userId) {
        return Vote.builder()
                .userId(userId)
                .feedId(UUID.randomUUID().toString())
                .username(username)
                .title(voteResponse.getTitle())
                .description(voteResponse.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }


    public Vote voteUserToEntity(VoteDTO voteResponse, String  username, String userId) {
        return Vote.builder()
                .userId(userId)
                .feedId(UUID.randomUUID().toString())
                .username(username)
                .title(voteResponse.getTitle())
                .description(voteResponse.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }
    public VoteDTO voteDTOToAnalytics(VoteDTO vote , String username, String userId) {
        return VoteDTO.builder()
                .userId(userId)
                .feedUID(vote.getFeedUID())
                .username(username)
                .title(vote.getTitle())
                .selectedOption(vote.getSelectedOption())
                .description(vote.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }


    public VoteDTO voteToDTO(Vote vote , String username, String userId) {
        return VoteDTO.builder()
                .userId(userId)
                .username(username)
                .title(vote.getTitle())
                .category("투표")
                .description(vote.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VoteDTO voteToDocument(VoteDTO voteResponse, String feedUID , String username, String userId,Long id) {
        return VoteDTO.builder()
                .id(id)
                .userId(userId)
                .feedUID(feedUID)
                .username(username)
                .title(voteResponse.getTitle())
                .description(voteResponse.getDescription())
                .voteType(voteResponse.getVoteType())
                .selectedOption(voteResponse.getSelectedOption())
                .createdAt(LocalDateTime.now())
                .build();
    }


    public  List<VoteDTO> voteDocumentToDTOList(List<VoteDocument> voteDocument) {
        return voteDocument.stream()
                .map(vote-> VoteDTO.builder()
//                        .id(vote.getId())
                        .userId(vote.getUserId())
                        .feedUID(vote.getFeedUID())
                        .username(vote.getUsername())
                        .title(vote.getTitle())
                        .description(vote.getDescription())
                        .voteType(vote.getVoteType())
                        .selectedOption(vote.getSelectedOption())
                        .createdAt(vote.getCreatedAt())
                        .build())
                        .collect(Collectors.toList());}



    public VoteDTO userVoteToDTO(UserVote vote , String username, String userId) {
        return VoteDTO.builder()
                .id(vote.getId())
                .voteId(vote.getVoteId())
                .userId(userId)
                .username(username)
                .upvote(vote.isUpvote())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VoteDTO EntityToVoteDTO(Vote vote) {
        return VoteDTO.builder()
                .id(vote.getId())
                .username(vote.getUsername())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .createdAt(vote.getCreatedAt())
                .build();
    }

    public VoteDTO DocumentToVoteDTO(VoteDocument vote) {
        return VoteDTO.builder()
                .id(vote.getId())
                .feedUID(vote.getFeedUID())
                .userId(vote.getUserId())
                .username(vote.getUsername())
                .voteType(vote.getVoteType())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .selectedOption(vote.getSelectedOption())
                .createdAt(vote.getCreatedAt())
                .build();
    }


    public List<VoteDTO> voteToDTOMainList(List<Vote> vote) {
        return vote.stream()
                .map(vote1 -> VoteDTO.builder()
                        .id(vote1.getId())
                        .username(vote1.getUsername())
                        .title(vote1.getTitle())
                        .description(vote1.getDescription())
                        .createdAt(vote1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public  UserVote userVoteToEntity(VoteDTO voteResponse, String  username, String userId) {
        return UserVote.builder()
                .voteId(voteResponse.getVoteId())
                .userId(userId)
                .upvote(voteResponse.getUpvote())
                .username(username)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public List<ReplyRequest> ReplyListToDTO(List<Reply> reply) {
        return reply.stream()
                .map(reply1 -> ReplyRequest.builder()
                        .feedUID(reply1.getFeedUID())
                        .commentUID(reply1.getCommentUID())
                        .username(reply1.getUsername())
                        .content(reply1.getContent())
                        .likeCount(reply1.getLikeCount())
                        .createdAt(reply1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
