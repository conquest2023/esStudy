package es.board.controller.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateExamStatsDTO {
    private String statisYy;
    private String jmCd;
    private String jmFldNm;
    private String grdCd;
    private String grdNm;
    private String emqualCd;
    private String emqualDispNm;
    private Integer lastRsltExamCnt;
    private Integer lastRsltPassCnt;
    private Float lastRsltPassRate;
    private Integer pilExamCnt;
    private Integer pilExemSilPassCnt;
    private Float pilExemSilPassRate;
    private Integer pilPassCnt;
    private Float pilPassRate;
    private Integer pilPassSilPassCnt;
    private Float pilPassSilPassRate;
    private Integer pilRecptCnt;
    private Integer silExamCnt;
    private Integer silExamPilExemCnt;
    private Integer silExamPilPassCnt;
    private Integer silPassCnt;
    private Float silPassRate;
    private Integer silRecptCnt;
    private Integer silRecptPilExemCnt;
    private Integer silRecptPilPassCnt;
}
