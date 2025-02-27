package es.board.repository.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "certificate")
public class Certificate {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String jmcd;


    private String jmfldnm;

    @Field(type = FieldType.Keyword)
    private String mdobligfldcd;

    @Field(type = FieldType.Text)
    private String mdobligfldnm;

    @Field(type = FieldType.Keyword)
    private String obligfldcd;

    @Field(type = FieldType.Text)
    private String obligfldnm;

    @Field(type = FieldType.Keyword)
    private String qualgbcd;

    @Field(type = FieldType.Text)
    private String qualgbnm;

    @Field(type = FieldType.Keyword)
    private String seriescd;

    @Field(type = FieldType.Text)
    private String seriesnm;
}
