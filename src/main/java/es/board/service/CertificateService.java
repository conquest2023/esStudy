package es.board.service;

import es.board.controller.model.req.CertificateDTO;
import es.board.repository.document.Certificate;

public interface CertificateService {

    Certificate getCertificate(String text);

}
