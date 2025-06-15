package es.board.controller.model.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DailyCheckRequest {
    private String category;

    private String matter;

    private String answer;

    private String correct;
    // getters, setters


    @Override
    public String toString() {
        return "DailyCheckRequest{" +
                "category='" + category + '\'' +
                ", matter='" + matter + '\'' +
                ", answer='" + answer + '\'' +
                ", correct='" + correct + '\'' +
                '}';
    }
}
