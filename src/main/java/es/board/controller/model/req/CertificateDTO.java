package es.board.controller.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDTO {
    private String jmcd;
    private String jmfldnm;
    private String mdobligfldcd;
    private String mdobligfldnm;
    private String obligfldcd;
    private String obligfldnm;
    private String qualgbcd;
    private String qualgbnm;
    private String seriescd;
    private String seriesnm;
}
