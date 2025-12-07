package es.board.infrastructure.es.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;

@Data
@Document(indexName = "view-events-*")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class View {

    @Id
    private String id; // 별도 ID 필드가 없다면 eventId를 사용하거나 자동생성

    @Field(type = FieldType.Date, name = "@timestamp")
    private Instant timestamp;

    @Field(type = FieldType.Keyword)
    private String aggregateId;

    @Field(type = FieldType.Keyword)
    private String authorId;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String contentHash;

    @Field(type = FieldType.Integer)
    private Integer contentLength;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    @Field(type = FieldType.Date)
    private Instant createdAtLocal;

    @Field(type = FieldType.Integer)
    private Integer createdHourLocal;

    @Field(type = FieldType.Keyword)
    private String docType;

    @Field(type = FieldType.Keyword)
    private String eventId;

    @Field(type = FieldType.Integer)
    private Integer eventVersion;

    @Field(type = FieldType.Text)
    private String excerpt;

    // 중첩 객체 매핑
    @Field(type = FieldType.Object)
    private InitialScores initialScores;

    @Field(type = FieldType.Date)
    private Instant occurredAt;

    private String lastEventId;


    @Field(type = FieldType.Keyword)
    private List<String> outboundLinks;

    @Field(type = FieldType.Keyword)
    private String postId;

    @Field(type = FieldType.Keyword)
    private String producer;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Integer)
    private Integer tokenCount;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String username;

    // --- 추가된 시청 정보 필드 ---
    @Field(type = FieldType.Date)
    private Instant viewedAt;

    @Field(type = FieldType.Date)
    private Instant viewedAtLocal;

    @Field(type = FieldType.Keyword)
    private String viewedAtLocalStr;

    @Field(type = FieldType.Integer)
    private Integer viewedHourLocal;

    @Field(type = FieldType.Keyword)
    private String viewerId;

    @Field(type = FieldType.Keyword)
    private String viewerName;

    @Field(type = FieldType.Keyword)
    private String visibility;

    // Inner Class for InitialScores
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitialScores {
        private Integer comment;
        private Integer like;
        private Integer reply;
        private Integer view;
    }
}