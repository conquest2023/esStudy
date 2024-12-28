package es.board.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.board.ex.IndexException;
import es.board.model.res.FeedCreateResponse;
import es.board.model.res.LoginResponse;
import es.board.model.res.SignUpResponse;
import es.board.repository.UserDAO;
import es.board.repository.document.Board;
import es.board.repository.document.EsUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserDAOImpl implements UserDAO {

    private final ElasticsearchClient client;
    @Override
    public SignUpResponse createUser(SignUpResponse dto)  {
        try {
            IndexResponse response = client.index(i -> i
                    .index("user")
                    .document(dto));
            return dto;
        } catch (IOException e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the document", e); // 예외를 커스텀 예외로 던짐
        }
    }
    @Override
    public EsUser login(LoginResponse login) {
        try {
            SearchResponse<EsUser> response = client.search(s -> s
                            .index("user")
                            .query(q -> q.term(t->t
                                    .field("userId.keyword")
                                    .value(login.getUserId())))
                            .query(q->q.term(t->t
                                    .field("password.keyword")
                                    .value(login.getPassword()))),
                    EsUser.class);
            if (response.hits().hits().isEmpty()) {

                log.warn("아이디와 비밀번호가 일치하지않습니다");
                return null;
            }
            return response.hits().hits().get(0).source();
        } catch (IOException e) {
            log.error("Error fetching recent feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch recent feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }

    @Override
    public void signUp() {

    }

    @Override
    public Boolean checkUserId(SignUpResponse sign) {
        try {
            SearchResponse<EsUser> response = client.search(s -> s
                            .index("user")
                            .query(q -> q.term(t -> t
                                    .field("userId") // keyword 필드 사용
                                    .value(sign.getUserId())
                            )),
                    EsUser.class
            );
            log.info(response.hits().hits().toString());
            if (response.hits().hits().isEmpty()) {
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Error fetching recent feed: {}", e.getMessage(), e);
            throw new IndexException("Failed to fetch recent feed", e); // 예외를 커스텀 예외로 감싸서 던짐
        }
    }
}
