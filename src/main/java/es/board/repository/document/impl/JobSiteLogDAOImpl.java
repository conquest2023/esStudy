package es.board.repository.document.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.controller.model.req.TopWriter;
import es.board.controller.model.res.JobSiteLogDTO;
import es.board.ex.IndexException;
import es.board.repository.JobSiteLogDAO;
import es.board.repository.document.Board;
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
public class JobSiteLogDAOImpl implements JobSiteLogDAO {

    private  final ElasticsearchClient client;

    @Override
    public void saveJobSiteLog(JobSiteLogDTO dto) {
        log.info(dto.toString());
        try{
            IndexResponse response=client.index(i->i
                    .index("job_site_log")
                    .document(dto));
        }catch (IOException e){
            log.error("존재하지 않는 인덱스입니다");
            throw  new IndexException("존재하지 않는 인덱스입니다");
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
