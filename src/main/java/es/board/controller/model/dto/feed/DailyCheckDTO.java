package es.board.controller.model.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DailyCheckDTO {
    private String category;

    private String matter;

    private String answer;

    private String correct;
    // getters, setters


    @Override
    public String toString() {
        return "DailyCheckDTO{" +
                "category='" + category + '\'' +
                ", matter='" + matter + '\'' +
                ", answer='" + answer + '\'' +
                ", correct='" + correct + '\'' +
                '}';
    }
}
