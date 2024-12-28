package es.board.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.board.ex.IndexException;
import es.board.model.res.LoginResponse;
import es.board.repository.UserDAO;
import es.board.repository.document.Board;
import es.board.repository.document.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserDAOImpl implements UserDAO {

    private final ElasticsearchClient client;
    @Override
    public User login(LoginResponse login) {
        try {
            SearchResponse<User> response = client.search(s -> s
                            .index("user")
                            .query(q -> q.term(t->t
                                    .field("userId")
                                    .value(login.getUserId())))
                            .query(q->q.term(t->t
                                    .field("password")
                                    .value(login.getPassword()))),
                    User.class);
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
}
