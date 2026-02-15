package es.board.domain.english;

import es.board.infrastructure.english.entity.AggEnglishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnglishAggService {

    private final AggEnglishRepository aggEnglishRepository;


    public int getTotalEnglishAttemptToday(String userId,String category){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ago = now.minusDays(1);
        return aggEnglishRepository.countByAttemptEnglish(userId,category, ago);
    }

//    public int getTotalVocabAttemptToday(String userId,String part){
//
//        return aggEnglishRepository.countByAttemptVocab(userId,part, LocalDateTime.now());
//    }

}
