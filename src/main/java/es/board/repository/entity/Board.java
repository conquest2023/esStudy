package es.board.repository.entity;



import es.board.model.res.FeedSaveDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.stereotype.Service;

@Document(indexName = "board")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setting(settingPath = "elastic/post-setting.json")
@Mapping(mappingPath = "elastic/post-mapping.json")
public class Board {

    @Id
    @Field(name="id", type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;




    public Board BoardToEntity(FeedSaveDTO   feedSaveDTO) {
        return Board.builder()
                .id(feedSaveDTO.getId())
                .username(feedSaveDTO.getUsername())
                .title(feedSaveDTO.getTitle())
                .description(feedSaveDTO.getDescription())
                .build();

    }
}
