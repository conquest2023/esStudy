package es.board.controller.model.mapper;

import es.board.controller.model.dto.feed.LikeDto;
import es.board.controller.record.LikeTargetInfo;
import es.board.domain.enum_type.TargetType;
import es.board.infrastructure.entity.feed.LikeEntity;
import es.board.domain.Like;
import es.board.repository.entity.Notification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class LikeMapper {

    public static Like toDomain(LikeEntity dto) {
        if (dto == null) return null;


        return Like.of(
                dto.getId(),
                dto.getPostId(),
                dto.getUserId(),
                dto.getTargetId(),
                dto.getTargetType(),
                dto.getCreatedAt()
        );
    }
    public static List<Like> toDomainList(List<LikeEntity> entities) {
        if (entities == null) return Collections.emptyList();

        return entities.stream()
                .map(LikeMapper::toDomain)   // Entity → Domain
                .toList();
    }
    public static List<LikeDto.Response> toRequestList(List<Like> domains, String userId) {
        if (domains == null) return Collections.emptyList();

        return domains.stream()
                .map(like -> new LikeDto.Response(
                        like.getId(),
                        like.isOwner(userId),
                        like.getPostId(),
                        like.getTargetId(),
                        like.getTargetType()
                ))
                .toList();
    }

    public static Notification toEntity(String username, TargetType targetType, LikeTargetInfo info,String msg){
        String type = switch (targetType) {
            case POST -> "게시글";
            case COMMENT -> "댓글";
            case REPLY -> "답글";
            default -> throw new IllegalArgumentException("지원하지 않는 TargetType: " + targetType);
        };
        if (targetType.toString().equals("게시글")){
            type="게시글";
        }
        if (targetType.toString().equals("댓글")){
            type="댓글";
        }
        if (targetType.toString().equals("답글")){
            type="답글";
        }
        return Notification.builder()
                .username(username)
                .userId(info.ownerId())
                .postId(info.postId())
                .type(type)
                .message(msg)
                .isCheck(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static LikeEntity toEntity(Like like) {
        if (like == null) return null;

        return LikeEntity.builder()
                .id(like.getId())
                .postId(like.getPostId())
                .userId(like.getUserId())
                .targetId(like.getTargetId())
                .targetType(like.getTargetType())
                .createdAt(like.getCreatedAt())
                .build();
    }

    public static LikeEntity toNewEntity(Like like) {
        if (like == null) return null;

        return LikeEntity.builder()
                .userId(like.getUserId())
                .targetId(like.getTargetId())
                .targetType(like.getTargetType())
                .build(); // createdAt, id는 DB 자동
    }


//    public static LikeDto.Response toResponse(Like like) {
//        if (like == null) return null;
//
////        int id = like.getId() == null ? 0 : like.getId().intValue();
//
//        return new LikeDto.Response(
//                like.getPostId(),
//                like.getUserId(),
//                like.getTargetId(),
//                like.getTargetType(),
//                like.getCreatedAt()
//        );
//    }

    // Entity 바로 → DTO(Response) (편의용)
//    public static LikeDto.Response toResponse(LikeEntity entity) {
//        if (entity == null) return null;
//
//        int id = entity.getId() == null ? 0 : entity.getId().intValue();
//
//        return new LikeDto.Response(
//                id,
//                entity.getUserId(),
//                entity.getTargetId(),
//                entity.getTargetType(),
//                entity.getCreatedAt()
//        );
//    }
}
