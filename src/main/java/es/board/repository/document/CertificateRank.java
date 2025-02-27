package es.board.repository.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "certificate_rank")
public class CertificateRank {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String statisYy;

    @Field(type = FieldType.Keyword)
    private String sumYy;

    @Field(type = FieldType.Keyword)
    private String jmCd;


    private String jmFldNm;

    @Field(type = FieldType.Keyword)
    private String grdCd;

    @Field(type = FieldType.Keyword)
    private String grdNm;

    @Field(type = FieldType.Keyword)
    private String instiCd;

    @Field(type = FieldType.Integer)
    private Integer examRecptCnt;

    @Field(type = FieldType.Integer)
    private Integer examRecptRank;

    @Field(type = FieldType.Integer)
    private Integer preyyExamRecptCnt;

    @Field(type = FieldType.Float)
    private Float preyyExamRecptIncRate;
}
