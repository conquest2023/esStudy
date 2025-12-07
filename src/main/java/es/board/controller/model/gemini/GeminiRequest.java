package es.board.controller.model.gemini;

import lombok.Data;

import java.util.List;


@Data
public class GeminiRequest {


    private List<Content> contents;

    public GeminiRequest(String text) {
        this.contents = List.of(new Content(List.of(new Part(text))));
    }

    public static class Content {
        private List<Part> parts;
        public Content(List<Part> parts) { this.parts = parts; }
        public List<Part> getParts() {
            return parts;
        }
    }

    public static class Part {
        private String text;
        public Part(String text) { this.text = text; }
        public String getText() { return text; }
    }
}