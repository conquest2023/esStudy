package es.board.repository.document.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import es.board.ex.IndexException;
import es.board.repository.CertificateDAO;
import es.board.repository.document.Certificate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.bouncycastle.cert.cmp.CertificateStatus;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CertificateDAOImpl  implements CertificateDAO {

    private final ElasticsearchClient client;

    @Override
    public List<Certificate> findSearchCertificate(String text) {
        try {
            SearchResponse<Certificate> response = client.search(s -> s
                            .index("certificate")
                            .query(q -> q
                                    .bool(b -> b
                                            .should(a -> a.match(t -> t
                                                    .field("jmfldnm")
                                                    .query(text))))),
                    Certificate.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error searching documents by Certificate: {}", e.getMessage(), e);
            throw new IndexException("자격증 검색 실패", e);
        }
    }


    @Override
        public List<String> findTop5CertificateCount() {
            try {
                SearchResponse<Void> response = client.search(s -> s
                                .index("certificate_states")
                                .size(0)
                                .aggregations("top_5_certificates", a -> a
                                        .terms(t -> t
                                                .field("jmFldNm.keyword")
                                                .size(5)
                                        )
                                ),
                        Void.class
                );

                List<String> topCertificates = new ArrayList<>();
                Map<String, Aggregate> aggs = response.aggregations();

                if (aggs != null && aggs.containsKey("top_5_certificates")) {
                    Aggregate top5Agg = aggs.get("top_5_certificates");

                    if (top5Agg.sterms() != null) {
                        for (StringTermsBucket bucket : top5Agg.sterms().buckets().array()) {
                            topCertificates.add((bucket.key().stringValue()));
                        }
                    }
                }

                return topCertificates;

            } catch (IOException e) {
                log.error("Elasticsearch 검색 오류: {}", e.getMessage(), e);
                throw new RuntimeException("자격증 검색 실패", e);
            }
        }
    @Override
    public Certificate findSearchCertificateByName(String name) {
        try {
            SearchResponse<Certificate> response = client.search(s -> s
                            .index("certificate")
                            .query(q -> q
                                    .term(t -> t
                                            .field("jmfldnm.keyword")
                                            .value(name)
                                    )
                            )
                            .size(1),
                    Certificate.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .findFirst()
                    .orElse(null);

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 오류", e);
        }
    }

    @Override
    public List<String> getCertificationNames() {
        try {
            SearchResponse<Certificate> response = client.search(s -> s
                            .index("certificate")
                            .query(q -> q.matchAll(m -> m))
                            .size(600),
                    Certificate.class);



            // 데이터를 변환하여 리스트로 반환
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(Certificate::getJmfldnm)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("❌ Elasticsearch 자격증 검색 실패: {}", e.getMessage(), e);
            throw new IndexException("자격증 검색 실패", e);
        }
    }
}





