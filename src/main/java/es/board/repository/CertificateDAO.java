package es.board.repository;

import es.board.repository.document.Certificate;

import java.util.List;

public interface CertificateDAO {



    Certificate findSearchCertificate(String text);
}
