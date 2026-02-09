package es.board.domain.english;

import es.board.infrastructure.english.collcetion.ProblemRepository;
import es.board.infrastructure.english.collcetion.EnglishProblem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnglishService {

    private final MongoTemplate mongoTemplate;

    private final ProblemRepository problemRepository;


    public List<EnglishProblem> findProblemList(){
        return problemRepository.findAll();
    }

    public Page<EnglishProblem> getProblemsWithPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return problemRepository.findAll(pageable);
    }

    public List<EnglishProblem> getRcProblemNoOffset(String lastId, int size) {
        Query query = new Query().limit(size);
        query.with(Sort.by(Sort.Direction.DESC, "_id"));

        if (lastId != null && !lastId.isEmpty()) {

            query.addCriteria(Criteria.where("_id").lt(lastId));
        }
        return mongoTemplate.find(query, EnglishProblem.class);
    }

    public List<EnglishProblem> getRandomProblems( int size) {
        // 1. 제외할 ID 리스트를 ObjectId로 변환
//        List<ObjectId> objectIds = excludeIds.stream()
//                .map(ObjectId::new)
//                .collect(Collectors.toList());
//
//        // 2. 조건 설정: excludeIds에 포함되지 않는($nin) 데이터만 매칭
//        MatchOperation matchStage = Aggregation.match(Criteria.where("_id").nin(objectIds));

        // 3. 랜덤 샘플링 설정: MongoDB의 $sample 사용 (성능이 매우 빠름)
        SampleOperation sampleStage = Aggregation.sample(size);

        // 4. 파이프라인 실행
        Aggregation aggregation = Aggregation.newAggregation(sampleStage);

        return mongoTemplate.aggregate(aggregation, "problems", EnglishProblem.class).getMappedResults();
    }

}
