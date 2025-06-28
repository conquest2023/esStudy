package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.ex.IndexException;
import es.board.repository.InterviewQuestionDAO;
import es.board.repository.document.InterviewQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InterviewQuestionDAOImpl implements InterviewQuestionDAO {

    private final ElasticsearchClient client;


    @Override
    public List<InterviewQuestion> searchInterviewQuestion(String text) {
        try {
            SearchResponse<InterviewQuestion> response = client.search(s -> s
                            .index("interview_questions")
                            .query(q -> q
                                    .bool(b -> b
                                            .should(
                                                    a -> a.match(t -> t
                                                            .field("question")
                                                            .query(text))))),
                    InterviewQuestion.class);
            log.info(response.toString());
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching documents by text: {}", e.getMessage(), e);
            throw new IndexException("Failed to search feed by text", e);
        }
    }

}
