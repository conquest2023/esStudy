package es.board.repository.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "certificate_exam_stats")
public class CertificateExamStats {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String statisYy;

    @Field(type = FieldType.Keyword)
    private String jmCd;


    private String jmFldNm;

    @Field(type = FieldType.Keyword)
    private String grdCd;

    @Field(type = FieldType.Text)
    private String grdNm;

    @Field(type = FieldType.Keyword)
    private String emqualCd;

    @Field(type = FieldType.Text)
    private String emqualDispNm;

    @Field(type = FieldType.Integer)
    private Integer lastRsltExamCnt;

    @Field(type = FieldType.Integer)
    private Integer lastRsltPassCnt;

    @Field(type = FieldType.Float)
    private Float lastRsltPassRate;

    @Field(type = FieldType.Integer)
    private Integer pilExamCnt;

    @Field(type = FieldType.Integer)
    private Integer pilExemSilPassCnt;

    @Field(type = FieldType.Float)
    private Float pilExemSilPassRate;

    @Field(type = FieldType.Integer)
    private Integer pilPassCnt;

    @Field(type = FieldType.Float)
    private Float pilPassRate;

    @Field(type = FieldType.Integer)
    private Integer pilPassSilPassCnt;

    @Field(type = FieldType.Float)
    private Float pilPassSilPassRate;

    @Field(type = FieldType.Integer)
    private Integer pilRecptCnt;

    @Field(type = FieldType.Integer)
    private Integer silExamCnt;

    @Field(type = FieldType.Integer)
    private Integer silExamPilExemCnt;

    @Field(type = FieldType.Integer)
    private Integer silExamPilPassCnt;

    @Field(type = FieldType.Integer)
    private Integer silPassCnt;

    @Field(type = FieldType.Float)
    private Float silPassRate;

    @Field(type = FieldType.Integer)
    private Integer silRecptCnt;

    @Field(type = FieldType.Integer)
    private Integer silRecptPilExemCnt;

    @Field(type = FieldType.Integer)
    private Integer silRecptPilPassCnt;
}
