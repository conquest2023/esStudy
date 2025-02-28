package es.board.service.impl;

import es.board.controller.model.req.CertificateDTO;
import es.board.repository.CertificateDAO;
import es.board.repository.document.Certificate;
import es.board.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.cert.cmp.CertificateStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {


    private  final CertificateDAO certificateDAO;

    @Override
    public List<String> getTop5CertificateCount() {
        return certificateDAO.findTop5CertificateCount();
    }

    @Override
    public Certificate getCertificate(String text) {
        return certificateDAO.findSearchCertificate(text);
    }
}
