package es.board.service;

import es.board.controller.model.req.CertificateDTO;
import es.board.repository.document.Certificate;
import es.board.repository.entity.CertificationSchedule;
import org.bouncycastle.cert.cmp.CertificateStatus;

import java.util.List;

public interface CertificateService {

    List<CertificationSchedule> getCertificationSchedule(String text);
    List<String> getTop5CertificateCount();
    List<Certificate> getCertificate(String text ,String userIp);

}
