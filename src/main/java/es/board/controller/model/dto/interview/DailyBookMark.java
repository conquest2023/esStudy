package es.board.controller.model.dto.interview;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBookMark {

    private String  userId;

    private String  username;

    private String category;


    private Long questionId;


    private LocalDateTime createdAt;

}
