package es.board.controller.model.mapper;

import es.board.config.jwt.JwtTokenProvider;
import es.board.controller.model.dto.interview.InterviewLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InterviewLogFactory {

    private final JwtTokenProvider jwtTokenProvider;

    public InterviewLogDTO createFrom(InterviewLogDTO input, String token) {
        String userId = "anonymous";
        String username = "익명";

        if (token != null) {
            token=token.substring(7);
            if (jwtTokenProvider.validateToken(token)){
            try {
                userId = jwtTokenProvider.getUserId(token);
                username = jwtTokenProvider.getUsername(token);
            } catch (Exception e) {
            }
        }
        }
        return InterviewLogDTO.builder()
                .eventType(input.getEventType())
                .query(input.getQuery())
                .category(input.getCategory())
                .subCategory(input.getSubCategory())
                .targetId(input.getTargetId())
                .userId(userId)
                .username(username)
                .id(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();
    }
}