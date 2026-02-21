package es.board.domain.english;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.lambda.functionName}")
    private String functionName;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public List<String> getPresignedUrls(List<String> s3Keys) {
        try {
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("bucket", bucketName);
            payloadMap.put("s3_keys", s3Keys);
            String payload = objectMapper.writeValueAsString(payloadMap);

            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build();

            InvokeResponse response = lambdaClient.invoke(request);
            String responseJson = response.payload().asUtf8String();

            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode body = objectMapper.readTree(root.path("body").asText());

            List<String> urls = new ArrayList<>();
            body.path("urls").forEach(node -> urls.add(node.asText()));

            return urls;

        } catch (Exception e) {
            throw new RuntimeException("람다 벌크 호출 중 에러: " + e.getMessage());
        }
    }
}
