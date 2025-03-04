package es.board.repository;

import es.board.repository.document.Certificate;
//import org.bouncycastle.cert.cmp.CertificateStatus;

import java.util.List;

public interface CertificateDAO {

    Certificate findSearchCertificateByName(String name);

    List<Certificate> findSearchCertificate(String text);

    List<String> findTop5CertificateCount();
}
