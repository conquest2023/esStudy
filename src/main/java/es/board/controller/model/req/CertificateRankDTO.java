package es.board.controller.model.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateRankDTO {
    private String statisYy;
    private String sumYy;
    private String jmCd;
    private String jmFldNm;
    private String grdCd;
    private String grdNm;
    private String instiCd;
    private Integer examRecptCnt;
    private Integer examRecptRank;
    private Integer preyyExamRecptCnt;
    private Float preyyExamRecptIncRate;
}