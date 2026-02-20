package es.board.domain.english;

import es.board.controller.model.dto.english.AudioDto;
import es.board.infrastructure.english.collcetion.Listen;
import es.board.mapper.document.english.AudioMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class SentenceService {

    private final MongoTemplate mongoTemplate;
    private final ConversationService conversationService;

    public List<AudioDto> getAudioUrls(String level) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("level").is(level)),
                Aggregation.sample(10)
        );
        List<Listen> listen = mongoTemplate.aggregate(
                aggregation, "listen", Listen.class).getMappedResults();
        List<String> s3Keys =listen.stream()
                .map(s -> s.getAudioInfo().getS3Key())
                .collect(Collectors.toList());

        List<String> presignedUrls = conversationService.getPresignedUrls(s3Keys);

        return AudioMapper.toDtoList(listen,presignedUrls);
    }
}