package es.board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;



@Configuration
public class ElasticConfig extends ElasticsearchConfiguration {
    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.elasticsearch.host}")
    private String esHost;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(esHost)
                .withBasicAuth(username, password)
                .withConnectTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }



}
