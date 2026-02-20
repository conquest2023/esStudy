package es.board.infrastructure.english.collcetion;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Data
@Document(collection = "listen")
public class Listen {

    @Id
    private String id;

    private String en;
    private String ko;
    private String category;
    private String level;

    @Field("normalized_en")
    private String normalizedEn;

    private List<String> tags;

    @Field("audio_info")
    private AudioInfo audioInfo;

    @Data
    public static class AudioInfo {
        @Field("s3_key")
        private String s3Key;
        @Field("voice_id")
        private String voiceId;

        private String gender;
    }
}