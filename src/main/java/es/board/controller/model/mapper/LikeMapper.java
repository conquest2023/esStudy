package es.board.controller.model.mapper;

import es.board.controller.model.dto.feed.LikeDto;
import es.board.repository.entity.feed.LikeEntity;
import es.board.service.domain.Like;

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
    public static List<LikeDto.Request> toRequestList(List<Like> domains,String userId) {
        if (domains == null) return Collections.emptyList();

        return domains.stream()
                .map(like -> new LikeDto.Request(
                        like.getId(),
                        like.isOwner(userId),
                        like.getPostId(),
                        like.getTargetId(),
                        like.getTargetType()
                ))
                .toList();
    }
//    public static Like toDomainForCreate(String userId, Long targetId, TargetType targetType) {
//        return Like.create(userId, targetId, targetType);
//    }

//    public static Like toDomain(LikeEntity entity) {
//        if (entity == null) return null;
//
//        return Like.of(
//                entity.getId(),
//                entity.getPostId(),
//                entity.getUserId(),
//                entity.getTargetId(),
//                entity.getTargetType(),
//                entity.getCreatedAt()
//        );
//    }


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


//    public static LikeDto.Request toResponse(Like like) {
//        if (like == null) return null;
//
////        int id = like.getId() == null ? 0 : like.getId().intValue();
//
//        return new LikeDto.Request(
//                like.getPostId(),
//                like.getUserId(),
//                like.getTargetId(),
//                like.getTargetType(),
//                like.getCreatedAt()
//        );
//    }

    // Entity 바로 → DTO(Request) (편의용)
//    public static LikeDto.Request toResponse(LikeEntity entity) {
//        if (entity == null) return null;
//
//        int id = entity.getId() == null ? 0 : entity.getId().intValue();
//
//        return new LikeDto.Request(
//                id,
//                entity.getUserId(),
//                entity.getTargetId(),
//                entity.getTargetType(),
//                entity.getCreatedAt()
//        );
//    }
}
