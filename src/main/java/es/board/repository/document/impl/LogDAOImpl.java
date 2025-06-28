package es.board.repository.document.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.ex.IndexException;
import es.board.repository.LogDAO;
import es.board.repository.document.InterviewLog;
import es.board.repository.document.InterviewQuestion;
import es.board.repository.document.JobSiteLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class LogDAOImpl<T> implements LogDAO<T> {

    private  final ElasticsearchClient client;

    @Override
    public  void saveLog(String indexName, T dto) {
        try {
             client.index(i -> i
                    .index(indexName)
                    .document(dto)
            );
        } catch (IOException e) {
            log.error("엘라스틱 인덱싱 실패: {}", e.getMessage());
            throw new IndexException("인덱싱에 실패했습니다.");
        }
    }
    @Override
    public List<String> aggregationInterviewQuestionLog(String subCategory) {
        try {
            SearchResponse<InterviewLog> response = client.search(s -> s
                            .index("interview_logs")
                            .size(0)
                            .query(q -> q
                            .bool(b -> b
                                    .filter(m -> m
                                            .term(t ->
                                                    t.field("subCategory")
                                                            .value(subCategory)))))
                            .aggregations("question_id", a -> a
                                    .terms(t -> t
                                            .field("targetId"))),
                    InterviewLog.class);
            List<StringTermsBucket> buckets = response.aggregations()
                    .get("question_id")
                    .sterms()
                    .buckets()
                    .array();
            return buckets.stream()
                    .map(bucket ->
                            bucket.key().stringValue())
                    .limit(5)
                    .toList();

        } catch (IOException e) {
            log.error("Error fetching all Log documents: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch all Log documents", e);
        }
    }

    @Override
    public List<JobSiteLog> findJobSiteLog() {
        try {
            SearchResponse<JobSiteLog> response = client.search(s -> s
                            .index("job_site_log")
                            .size(0)
                            .aggregations("top_sites", a -> a
                                    .terms(t -> t
                                            .field("siteName.keyword"))),
                    JobSiteLog.class);
            List<StringTermsBucket> buckets = response.aggregations()
                    .get("top_sites")
                    .sterms()
                    .buckets()
                    .array();
            return buckets.stream()
                    .map(bucket -> new JobSiteLog(
                            bucket.key().stringValue()))
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error fetching all Log documents: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch all Log documents", e);
        }
    }
}
