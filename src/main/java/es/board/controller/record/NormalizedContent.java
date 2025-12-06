package es.board.controller.record;

import java.util.List;

public record NormalizedContent(
        String contentHtml, String contentText, String excerpt,
        List<String> links, int contentLength, int tokenCount, String contentHash
) {}