package es.board.infrastructure.es.document;



import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "content_read")
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Data
@Builder
@ToString
public class Feed {

    @Id
    private String id;          // keyword
    private String docType;     // keyword: feed|comment|reply
    private String parentId;    // keyword
    private String category;    // keyword
    private String title;       // text (nori)
    private String content;     // text (nori)
    private String contentText; // text (nori)
    private String authorId;    // keyword
    private String username;
    private List<String> tags;

    private Integer likeCount;
    private Integer commentCount;
    private Integer replyCount;
    private String lastEventId;
    private Integer eventVersion;

//    @JsonSerialize(using = InstantSerializer.class)
//    @JsonDeserialize(using = InstantDe)
    private Instant createdAt;


//    @JsonSerialize(using = InstantSerializer.class)
//    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant updatedAt;  // date

//

//    private LocalDateTime createdAt;
//
//




}
