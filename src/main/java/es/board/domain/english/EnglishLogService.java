package es.board.domain.english;


import es.board.controller.model.dto.english.EnglishProblemAttemptDto;
import es.board.controller.model.dto.english.WrongNoteDto;
import es.board.infrastructure.english.collcetion.English_RC;
import es.board.infrastructure.english.collcetion.English_Vocab;
import es.board.infrastructure.english.collcetion.RcCollectionRepository;
import es.board.infrastructure.english.collcetion.VocabCollectionRepository;
import es.board.infrastructure.english.entity.EnglishProblemAttempt;
import es.board.infrastructure.english.entity.EnglishProblemAttemptRepository;
import es.board.infrastructure.english.entity.WrongNote;
import es.board.infrastructure.english.entity.WrongNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnglishLogService {

    private final EnglishProblemAttemptRepository attemptRepository;

    private final RcCollectionRepository rcCollectionRepository;

    private final VocabCollectionRepository vocabCollectionRepository;

    private final WrongNoteRepository wrongNoteRepository;

    @Transactional
    public void saveWrongNote(String userId, WrongNoteDto.Request request) {

            wrongNoteRepository.upsertWrongNote(
                    userId,
                    request.getObjectId(),
                    request.getCategory(),
                    request.getPart(),
                    request.getLevel().name()
            );
    }

    @Transactional
    public void saveEnglishLog(String userId, EnglishProblemAttemptDto.Request request) {

        EnglishProblemAttempt entity = EnglishProblemAttempt.toEntity(userId, request);

        attemptRepository.save(entity);
    }


    public List<? extends Object> getWrongNoteList(int page, int size,String category, String userId){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<WrongNote> wrongNote = wrongNoteRepository.findByUserIdAndResolvedFalseOrderByLastWrongAtDesc(userId,category, pageable);
        List<String> ids = wrongNote.stream().filter(
                        o -> o.getCategory().equals(category))
                .map(o -> o.getObjectId())
                .toList();
        if (category.equals("RC")){
            return rcCollectionRepository.findByIdIn(ids);
        }else {
            return vocabCollectionRepository.findProblemsByCustomIds(ids);
        }
    }
}
