package es.board.controller.model.mapper;

import es.board.controller.model.dto.feed.PostDTO;
import es.board.controller.model.dto.feed.NoticeDTO;
import es.board.controller.model.dto.feed.VoteDTO;
import es.board.repository.document.Board;
import es.board.repository.document.VoteDocument;
import es.board.repository.entity.*;
import es.board.infrastructure.entity.feed.NoticeEntity;
import es.board.infrastructure.entity.feed.PostEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Component
@Slf4j
public class FeedMapper {
    public PostEntity toPostEntity(PostDTO.Request feedSaveDTO) {
        return PostEntity.builder()
//                .userId(feedSaveDTO.getUserId())
//                .feedUID(UUID.randomUUID().toString())
                .username(feedSaveDTO.getUsername())
                .title(feedSaveDTO.getTitle())
                .anonymous(feedSaveDTO.isAnonymous())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Board toBoardDocument(PostDTO.Request response, int id , String feedUID) {
        return Board.builder()
                .feedUID(feedUID)
                .id(id)
//                .userId(response.getUserId())
                .username(response.getUsername())
                .title(response.getTitle())
                .description(response.getDescription().replace("\\n", "\n"))
                .category(response.getCategory())
                .viewCount(response.getViewCount())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<PostDTO.Response> fromBoardDtoList(List<Board> boards) {
        return boards.stream()
                .map(board -> PostDTO.Response.builder()
//                        .feedUID(board.getFeedUID())
                        .id(board.getId())
                        .username(board.getUsername())
                        .title(board.getTitle())
                        .description(board.getDescription())
//                        .likeCount(board.getLikeCount())
                        .category(board.getCategory())
                        .viewCount(board.getViewCount())
                        .createdAt(board.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public PostDTO.Response fromBoardDto(Board board) {
        return PostDTO.Response.builder()
//                .feedUID(board.getFeedUID())
                .id(board.getId())
                .userId(board.getUserId())
                .username(board.getUsername())
                .title(board.getTitle())
                .description(board.getDescription())
                .category(board.getCategory())
                .viewCount(board.getViewCount())
//                .likeCount(board.getLikeCount())
                .createdAt(board.getCreatedAt())
                .build();
    }

//    public Likes toLikeEntity(String feedUID, String userId) {
//        return Likes.builder()
//                .feedUID(feedUID)
//                .userId(userId)
//                .created_at(LocalDateTime.now())
//                .build();
//    }

//    public  List<NoticeDTO.Request> fromNoticeList(List<NoticeEntity> notice) {
//        return notice.stream()
//                .map(notice1 -> NoticeDTO.Request.builder()
//                        .id(notice1.getId())
////                        .feedUID(notice1.getFeedUID())
//                        .username(notice1.getUsername())
//                        .title(notice1.getTitle())
//                        .description(notice1.getDescription())
//                        .createdAt(notice1.getCreatedAt())
//                        .build())
//                .collect(Collectors.toList());
//    }

    public static NoticeDTO.Request toNoticeDto(NoticeEntity notice) {
        return NoticeDTO.Request.builder()
//                .id(notice.getId())
//                .category(notice.getCategory())
//                .title(notice.getTitle())
////                .imageURL(notice.getImageURL())
//                .description(notice.getDescription().replace("\\n", "\n"))
//                .username(notice.getUsername())
//                .createdAt(notice.getCreatedAt())
//                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    public static NoticeEntity toNoticeEntity(NoticeDTO.Request notice, String userId) {
        return NoticeEntity.builder().build();
//                .id(notice.getId())
////                .feedUID(feedUID)
//                .category("공지사항")
//                .userId(userId)
//                .title(notice.getTitle())
//                .description(notice.getDescription().replace("\\n", "\n"))
////                .imageURL(notice.getImageURL())
//                .username("관리자")
//                .createdAt(LocalDateTime.now())
//                .build();
    }

//    public NoticeDTO.Request fromNoticeDocument(NoticeEntity notice, String userId, String feedUID) {
//        return NoticeDTO.Request.builder()
//                .id(notice.getId())
//                .username("관리자")
//                .feedUID(feedUID)
//                .category("공지사항")
//                .userId(userId)
//                .title(notice.getTitle())
//                .description(notice.getDescription().replace("\\n", "\n"))
////                .imageURL(notice.getImageURL())
//                .createdAt(LocalDateTime.now())
//                .build();
//    }

    public Vote toVoteEntity(VoteDTO.Request voteResponse, String  username, String userId) {
        return Vote.builder()
                .userId(userId)
                .feedId(UUID.randomUUID().toString())
                .username(username)
                .title(voteResponse.getTitle())
                .description(voteResponse.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VoteDTO.Request changeVoteDTOToAnalytics(VoteDTO.Request vote , String username, String userId) {
        return VoteDTO.Request.builder()
                .userId(userId)
                .feedUID(vote.getFeedUID())
                .username(username)
                .title(vote.getTitle())
                .selectedOption(vote.getSelectedOption())
                .description(vote.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }


    public VoteDTO.Request fromVoteDto(Vote vote , String username, String userId) {
        return VoteDTO.Request.builder()
                .userId(userId)
                .username(username)
                .title(vote.getTitle())
                .category("투표")
                .description(vote.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VoteDTO.Request changeVoteDtoToDocument(VoteDTO.Request voteResponse, String feedUID , String username, String userId, Long id) {
        return VoteDTO.Request.builder()
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


    public  List<VoteDTO.Request> fromVoteDTOList(List<VoteDocument> voteDocument) {
        return voteDocument.stream()
                .map(vote-> VoteDTO.Request.builder()
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



    public VoteDTO.Request fromUserVoteDTO(UserVote vote , String username, String userId) {
        return VoteDTO.Request.builder()
                .id(vote.getId())
                .voteId(vote.getVoteId())
                .userId(userId)
                .username(username)
                .upvote(vote.isUpvote())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public VoteDTO.Request fromVoteDTO(Vote vote) {
        return VoteDTO.Request.builder()
                .id(vote.getId())
                .username(vote.getUsername())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .createdAt(vote.getCreatedAt())
                .build();
    }

    public VoteDTO.Request fromDocumentVoteDTO(VoteDocument vote) {
        return VoteDTO.Request.builder()
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


    public List<VoteDTO.Request> voteToDTOMainList(List<Vote> vote) {
        return vote.stream()
                .map(vote1 -> VoteDTO.Request.builder()
                        .id(vote1.getId())
                        .username(vote1.getUsername())
                        .title(vote1.getTitle())
                        .description(vote1.getDescription())
                        .createdAt(vote1.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public  UserVote userVoteToEntity(VoteDTO.Request voteResponse, String  username, String userId) {
        return UserVote.builder()
                .voteId(voteResponse.getVoteId())
                .userId(userId)
                .upvote(voteResponse.getUpvote())
                .username(username)
                .createdAt(LocalDateTime.now())
                .build();
    }
//    public List<ReplyDTO.Response> fromReplyDtoList(List<Reply> reply) {
//        return reply.stream()
//                .map(reply1 -> ReplyDTO.Response.builder()
//                        .feedUID(reply1.getFeedUID())
//                        .commentUID(reply1.getCommentUID())
//                        .username(reply1.getUsername())
//                        .content(reply1.getContent())
//                        .likeCount(reply1.getLikeCount())
//                        .createdAt(reply1.getCreatedAt())
//                        .build())
//                .collect(Collectors.toList());
//    }
}
