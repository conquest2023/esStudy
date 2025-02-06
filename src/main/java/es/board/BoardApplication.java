package es.board;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "es.board.repository.entity.entityrepository") // JPA Repository로만 설정
@EnableRedisRepositories(basePackages = "es.board.config.redis")
public class BoardApplication {
	private final ElasticsearchClient client;
	@Autowired
	public BoardApplication(ElasticsearchClient client) {
		this.client = client;
	}

	public static void main(String[] args) {
		SpringApplication.run(BoardApplication.class, args);


	}


//	@PostConstruct
//	public void createIndex() throws Exception {
//		// 인덱스 생성 요청
//		CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
//				.index("test")  // 인덱스 이름 지정
//				.settings(IndexSettings.of(s -> s))  // 기본 설정
//				.mappings(mappings -> mappings
//						.properties("field1", p -> p
//								.text(t -> t)
//						)
//						.properties("field2", p -> p
//								.text(t -> t)
//						)
//						.properties("field3", p -> p
//								.text(t -> t)
//						)
//				)
//				.build();
//		CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
//
//		// 결과 출력
//
//	}
}
