package es.board.repository.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

//@Document(indexName = "todo_index")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "schedule_index") // ✅ Elasticsearch 인덱스 이름
public class Schedule {

    @Id
    @Field(type = FieldType.Keyword) // ✅ ID는 Keyword 타입
    private Long scheduleId;

    @Field(type = FieldType.Keyword)
    private String userId;

    @Field(type = FieldType.Text)
    private String title;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime startDatetime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endDatetime;

    @Field(type = FieldType.Boolean)
    private boolean allDay;

    @Field(type = FieldType.Text)
    private String location;

    @Field(type = FieldType.Text)
    private String description;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Boolean)
    private Boolean isRepeat; // 🔄 반복 일정 여부


    @Field(type = FieldType.Keyword) // ✅ 카테고리는 빠른 검색을 위해 Keyword 타입 사용
    private String category;


    @Field(type = FieldType.Text)
    private String repeatDays; // 🔄 반복 요일

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime repeatStartDate; // 🔄 반복 일정 시작 날짜

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime repeatEndDate; // 🔄 반복 일정 종료 날짜

}
