package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import es.board.ex.IndexException;
import es.board.controller.model.dto.feed.LoginDTO;
import es.board.controller.model.dto.feed.SignUpDTO;
import es.board.repository.UserDAO;
import es.board.repository.document.EsUser;
import es.board.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserDAOImpl implements UserDAO {

    private final ElasticsearchClient client;

    @Override
    public void createUser(User dto) {
        log.info(dto.toString());
        try {
            IndexResponse response = client.index(i -> i
                    .index("user")
                    .document(dto));
            log.info(response.toString());
        } catch (IOException e) {
            log.error("Error indexing document: {}", e.getMessage(), e);
            throw new IndexException("Failed to index the document", e); // 예외를 커스텀 예외로 던짐
        }
    }

    @Override
    public Long findVisitCount(String userId) {
        try {
            SearchResponse<EsUser> response = client.search(s -> s
                            .index("user")
                            .size(0)
                            .query(q -> q
                                    .bool(b->b.filter(f->f.term(t -> t
                                            .field("userId")
                                            .value((userId))))))
                    .source(a->a.filter(f->f.includes("visitCount"))).size(1),EsUser.class);
            log.info("findVisitCount={}",response.toString());
            return response.hits().hits().stream()
                    .findFirst()
                    .map(hit -> (long) hit.source().getVisitCount())
                    .orElse(0L);
        } catch (IOException e) {
            log.error("Error fetching VisitCount for userId {}: {}", userId, e.getMessage(), e);
            throw new IndexException("Failed to fetch visit count for userId: " + userId, e);
        }
    }
    @Override
    public User login(LoginDTO login) {
        try {
            SearchResponse<User> response = client.search(s -> s
                            .index("user")
                            .query(q -> q.term(t->t
                                    .field("userId.keyword")
                                    .value(login.getUserId())))
                            .query(q->q.term(t->t
                                    .field("password.keyword")
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
    public void updateVisitCount(String userId) {
        try {
            client.updateByQuery(u -> u
                    .index("user")
                    .query(q -> q
                            .bool(b->b.filter(f->f.term(t ->t
                                            .field("userId")
                                            .value(userId)))

                            )).script(s -> s
                            .source("""
                            if (ctx._source.visitCount == null) {
                                ctx._source.visitCount = 0;
                            }
                            ctx._source.visitCount += params.increment;
                        """)
                            .params(Map.of("increment", JsonData.of(1)))).refresh(true));
            log.info("방문 횟수 업데이트 성공");
        } catch (IOException e) {
            log.error("조회수 업데이트 실패: {}", e.getMessage());
            throw new IndexException(e);
        }
    }


    @Override
    public void signUp() {

    }

    @Override
    public Boolean checkUserId(SignUpDTO sign) {
        try {
            SearchResponse<User> response = client.search(s -> s
                            .index("user")
                            .query(q -> q.term(t -> t
                                    .field("userId") // keyword 필드 사용
                                    .value(sign.getUserId())
                            )),
                    User.class
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
