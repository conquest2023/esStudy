package es.board.controller.model.mapper;

import es.board.controller.model.dto.poll.PollDto;
import es.board.controller.model.dto.poll.PollOptionDTO;
import es.board.controller.model.dto.poll.PollVoteDTO;
import es.board.repository.entity.feed.PostEntity;
import es.board.repository.entity.poll.PollEntity;
import es.board.repository.entity.poll.PollOptionEntity;
import es.board.repository.entity.poll.PollVoteEntity;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
public final class PollDomainMapper {

    private PollDomainMapper() {
        // 유틸 클래스 - 인스턴스 생성 방지
    }

    // =========================================================
    // Poll: DTO.Request -> Entity
    // =========================================================
    public static PollEntity toEntity(PostEntity post, PollDto.Request dto) {


        PollEntity poll = PollEntity.builder()
                .post(post)
                .userId(post.getUserId())
                .multiSelect(dto.isMultiSelect())
                .maxSelectCnt(dto.getMaxSelectCnt())
                .anonymous(dto.isAnonymous())
                .closesAt(dto.getClosesAt())
                .build();

        // 옵션 매핑
        List<PollOptionEntity> options = toOptionEntities(dto.getOptions(), poll);
        options.forEach(poll::addPoll);
//        post.setPoll(poll);
        return poll;
    }


    // =========================================================
    // Poll: Entity -> DTO.Response
    // =========================================================
    public static PollDto.Response toPollRequest(PollEntity poll) {

        if (poll == null) {
            return null;
        }
        return PollDto.Response.builder()
                .pollId(poll.getId())
                .postId(poll.getPost().getId())
                .multiSelect(poll.isMultiSelect())
                .maxSelectCnt(poll.getMaxSelectCnt())
                .isAnonymous(poll.isAnonymous())
                .isAuthor(false)
                .closesAt(poll.getClosesAt())
                .createdAt(poll.getCreatedAt())
                .options(toOptionRequestDtos(poll.getOptions()))
                .votes(toVoteRequestDtos(poll.getVotes()))
                .build();
    }

    private static List<PollOptionDTO.Request> toOptionRequestDtos(List<PollOptionEntity> options) {
        if (options == null) return List.of();

        return options.stream()
                .map(opt -> PollOptionDTO.Request.builder()
                        .optionId(opt.getId())
                        .content(opt.getContent())
//                        .voteCount(opt.getVoteCount())
                        .build()
                )
                .toList();
    }

    private static List<PollVoteDTO.Response> toVoteRequestDtos(List<PollVoteEntity> votes) {
        if (votes == null) return List.of();

        return votes.stream()
                .map(v -> PollVoteDTO.Response.builder()
                        .voteId(v.getId())
                        .optionId(v.getOption().getId())
                        .userId(v.getVoterId())
                        .createdAt(v.getVotedAt())
                        .build()
                )
                .toList();
    }

    public static List<PollOptionEntity> toOptionResponseEntities(List<PollOptionDTO.Response> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }

        return options.stream()
                .map(PollDomainMapper::toOptionResponseEntity)
                .collect(Collectors.toList());
    }
    public static List<PollOptionEntity> toOptionResponsesEntity(List<PollOptionDTO.Response> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return options.stream()
                .map(PollDomainMapper::toOptionResponseEntity)
                .collect(Collectors.toList());
    }

    public static PollOptionEntity toOptionEntity(PollOptionDTO.Response dto, PollEntity poll) {
        if (dto == null || poll == null) {
            return null;
        }
        return PollOptionEntity.builder()
                .poll(poll)
                .content(dto.getContent())
                .sortOrder(dto.getSortOrder())
                .build();
    }

    public static List<PollOptionEntity> toOptionEntities
            (List<PollOptionDTO.Response> optionDTOs, PollEntity poll) {
        if (optionDTOs == null || optionDTOs.isEmpty()) {
            return Collections.emptyList();
        }
        return optionDTOs.stream()
                .map(dto -> toOptionEntity(dto, poll))
                .collect(Collectors.toList());

    }


    public static PollOptionEntity toOptionResponseEntity(PollOptionDTO.Response option) {
        if (option == null) {
            return null;
        }
//        long voteCount = 0L;
//        if (option.getVotes() != null) {
//            voteCount = option.getVotes().size();
//        }

        return PollOptionEntity.builder()
//                .id(option.get)
//                .poll(option.getPollId())
                .content(option.getContent())
                .sortOrder(option.getSortOrder())
                .createdAt(option.getCreatedAt())
                .build();
    }

//    public static List<PollOptionDTO.Response> toOptionResponses(List<PollOptionEntity> options) {
//        if (options == null || options.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        return options.stream()
//                .map(PollDomainMapper::toOptionResponseEntity)
//                .collect(Collectors.toList());
//    }

    // =========================================================
    // PollVote: DTO.Request -> Entity
    // (주의: Poll, PollOption 주입은 Service 레벨에서)
    // =========================================================
    public static PollVoteEntity toVoteEntity(PollVoteDTO.Response dto, PollEntity poll, PollOptionEntity option) {
        if (dto == null || poll == null || option == null) {
            return null;
        }

        return PollVoteEntity.builder()
                .poll(poll)
                .option(option)
                .voterId(dto.getUserId())
                .build();
    }

    // =========================================================
    // PollVote: Entity -> DTO.Response
    // =========================================================
    public static PollVoteEntity toVoteRequest(PollVoteDTO.Request request,String userId,PollEntity poll,PollOptionEntity option) {

        return PollVoteEntity.builder()
                .poll(poll)
                .option(option)
                .voterId(userId)
                .votedAt(LocalDateTime.now())
                .build();
    }

    public static List<PollVoteEntity> toVoteRequestList(
            PollVoteDTO.MultiRequest request,
            String userId,
            PollEntity poll,
            List<PollOptionEntity> options
    ) {
        // 옵션 ID → OptionEntity Map
        Map<Long, PollOptionEntity> optionMap = options.stream()
                .collect(Collectors.toMap(PollOptionEntity::getId, o -> o));
        return request.getOptionIds().stream()
                .map(optionId -> {
                    PollOptionEntity option = optionMap.get(optionId);
                    if (option == null) {
                        throw new IllegalArgumentException("존재하지 않는 옵션 ID입니다: " + optionId);
                    }
                    return PollVoteEntity.builder()
                            .poll(poll)
                            .option(option)
                            .voterId(userId)
                            .votedAt(LocalDateTime.now())
                            .build();
                })
                .collect(Collectors.toList());
    }


//    public static List<PollVoteDTO.Response> toVoteResponses(List<PollVoteEntity> votes) {
//        if (votes == null || votes.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        return votes.stream()
//                .map(PollDomainMapper::toVoteResponse)
//                .collect(Collectors.toList());
//    }
}
