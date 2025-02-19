package es.board.repository.document.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import es.board.ex.IndexException;
import es.board.repository.ToDoDAO;
import es.board.repository.document.Todo;
import es.board.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ToDoDAOImpl  implements ToDoDAO {

    private final ElasticsearchClient client;

    @Override
    public void savePercentTodo(List<Todo> todos) {
        try {
            // ✅ Bulk 요청 생성
            List<BulkOperation> operations = todos.stream()
                    .map(todo -> BulkOperation.of(b -> b.index(i -> i
                            .index("todo_index")
                            .document(todo))))
                    .collect(Collectors.toList());

            BulkRequest bulkRequest = BulkRequest.of(b -> b.operations(operations));
            BulkResponse response = client.bulk(bulkRequest);

            log.info("✅ Bulk Indexing 완료! 총 저장 개수: {}", todos.size());
        } catch (IOException e) {
            log.error("❌ Elasticsearch Bulk Indexing 실패: {}", e.getMessage(), e);
            throw new IndexException("Failed to bulk index the TodoDocument", e);
        }
    }
}

