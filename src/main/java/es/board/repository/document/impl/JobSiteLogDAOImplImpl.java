package es.board.repository.document.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

public class JobSiteLogDAOImplImpl extends JobSiteLogDAOImpl {
    public JobSiteLogDAOImplImpl(ElasticsearchClient client) {
        super(client);
    }
}
