package es.board.controller.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PushSubscriptionDto {

    private String endpoint;
    private Keys keys;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Keys {
        private String p256dh;
        private String auth;
    }
}