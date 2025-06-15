package es.board.ex.handler;

import es.board.controller.model.ErrorResult;
import es.board.ex.TokenInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TokenExceptionHandler {

    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ErrorResult> handleTokenError(TokenInvalidException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResult("권한이 없습니다", e.getMessage()));
    }


}
