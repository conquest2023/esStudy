package es.board.repository.document;


import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "user")
public class User {


    private  Long id;

    private String memberId;

    private String password;



}
