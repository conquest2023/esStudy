package es.board.controller.model.req;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TistoryPost {

    private String blogName;
    private String blogUrl;
    private String title;
    private String postUrl;
    private String description;
    private String date;
    private String thumbnailUrl;
}
