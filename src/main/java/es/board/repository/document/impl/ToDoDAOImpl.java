package es.board.repository.document.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import es.board.ex.IndexException;
import es.board.repository.ToDoDAO;
import es.board.repository.document.Todo;
import es.board.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ToDoDAOImpl  implements ToDoDAO {

    private final ElasticsearchClient client;
    @Override
    public void savePercentTodo(List<Todo> todo) {
            try {
                IndexResponse response = client.index(i -> i
                        .index("user")
                        .document(todo));
                log.info(response.toString());
            } catch (IOException e) {
                log.error("Error indexing document: {}", e.getMessage(), e);
                throw new IndexException("Failed to index the TodoDocument", e); // 예외를 커스텀 예외로 던짐
            }
        }
    }

