package es.board.controller.model.dto.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteMeta {
    private String name;
    private String logo;
    private String description;
    private String link;
    private List<String> category;
}