package es.board.controller.record;

import java.util.List;

public record MissingPollPayload(
        int count,
        List<MissingPollItem> items
) {}