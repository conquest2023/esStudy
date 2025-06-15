package es.board.ex.handler;


import es.board.controller.model.ErrorResult;
import es.board.ex.IndexException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class IndexExceptionHandler {


    @ExceptionHandler(IndexException.class)
    public ResponseEntity<ErrorResult> indexExHandler(IndexException e){
        log.info("[IndexExceptionError] ex",e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResult("DB 쪽 오류가 발생하였습니다", e.getMessage()));
    }

}
