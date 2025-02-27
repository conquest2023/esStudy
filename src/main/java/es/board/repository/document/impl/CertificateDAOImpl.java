package es.board.repository.document.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.ex.IndexException;
import es.board.repository.CertificateDAO;
import es.board.repository.document.Certificate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CertificateDAOImpl  implements CertificateDAO {

    private final ElasticsearchClient client;

    @Override
    public Certificate findSearchCertificate(String text) {
        try {
            SearchResponse<Certificate> response = client.search(s -> s
                            .index("certificate")
                            .query(q -> q
                                    .bool(b -> b
                                            .should(
                                                    a -> a.match(t -> t
                                                            .field("jmfldnm.keyword")
                                                            .query(text))))),
                    Certificate.class);
            log.info(response.toString());
            return response.hits().hits().get(0).source();
        } catch (IOException e) {
            log.error("Error searching documents by Certificate: {}", e.getMessage(), e);
            throw new IndexException("자격증 검색 실패", e);
        }
    }
}
