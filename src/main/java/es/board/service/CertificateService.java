package es.board.service;

import es.board.controller.model.req.CertificateDTO;
import es.board.repository.document.Certificate;
import org.bouncycastle.cert.cmp.CertificateStatus;

import java.util.List;

public interface CertificateService {


    List<String> getTop5CertificateCount();
    List<Certificate> getCertificate(String text ,String userIp);

}
