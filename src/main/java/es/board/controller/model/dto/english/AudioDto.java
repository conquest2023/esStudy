package es.board.controller.model.dto.english;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AudioDto {

    private String id;
    private String en;
    private String ko;
    private String category;
    private String level;
    private String normalizedEn;
    private List<String> tags;

    private String audioUrl;


}
