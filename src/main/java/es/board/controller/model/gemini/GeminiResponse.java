package es.board.controller.model.gemini;

import java.util.List;

public class GeminiResponse {

    public List<Candidate> candidates;

    public String getText() {
        return candidates.get(0).getContent().getParts().get(0).getText();
    }

    public static class Candidate {
        private Content content;
        public Content getContent() {
            return content;
        }
        public void setContent(Content content) {
            this.content = content;
        }
    }

    public static class Content {
        private List<Part> parts;
        public List<Part> getParts() {
            return parts;
        }
        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        private String text;
        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
    }
}
