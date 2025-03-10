package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import es.board.controller.model.req.VoteResponse;
import es.board.ex.IndexException;
import es.board.repository.VoteDAO;
import es.board.repository.entity.Vote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
@RequiredArgsConstructor
@Slf4j
public class VoteDAOImpl implements VoteDAO {

    private  final ElasticsearchClient client;

    @Override
    public void saveAgreeVote(VoteResponse voteResponse, Long id) {
        try {
            IndexResponse response = client.index(i -> i
                    .index("data_votes")
                    .id(String.valueOf(id))
                    .document(voteResponse));
        } catch (IOException e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the document", e);
        }
    }
}
