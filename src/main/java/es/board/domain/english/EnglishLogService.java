package es.board.domain.english;


import es.board.controller.model.dto.english.EnglishProblemAttemptDto;
import es.board.controller.model.dto.english.WrongNoteDto;
import es.board.infrastructure.english.entity.EnglishProblemAttemptRepository;
import es.board.infrastructure.english.entity.WrongNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnglishLogService {

    private final EnglishProblemAttemptRepository attemptRepository;
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
}
