package es.board.mapper.document.english;


import es.board.controller.model.dto.english.AudioDto;
import es.board.infrastructure.english.collcetion.Listen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AudioMapper {

    /**
     * 엔티티 객체를 DTO로 변환 (단건)
     */
    public static AudioDto toDto(Listen entity, String  audioUrl) {
        return new AudioDto(
                entity.getId(),
                entity.getEn(),
                entity.getKo(),
                entity.getCategory(),
                entity.getLevel(),
                entity.getNormalizedEn(),
                entity.getTags(),
                audioUrl
        );
    }


    public static List<AudioDto> toDtoList(List<Listen> entities, List<String> audioUrlsList) {
        if (entities.size() != audioUrlsList.size()) {
            throw new IllegalArgumentException("엔티티 리스트와 URL 리스트의 크기가 일치하지 않습니다.");
        }

        return IntStream.range(0, entities.size())
                .mapToObj(i -> toDto(entities.get(i), audioUrlsList.get(i)))
                .collect(Collectors.toList());
    }
}
