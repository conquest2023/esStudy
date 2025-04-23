package es.board.service;

import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public interface OpenGraphParserService {

    Map<String, String> extractOGMeta(String url);
}
