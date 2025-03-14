package com.example.ERPSpringBootBackEnd.exceptionHandler;

import com.example.ERPSpringBootBackEnd.dto.responseDto.ErrorResponseDto;
import com.example.ERPSpringBootBackEnd.exception.APIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Date;

/**
 * @author Rimi
 * @since 10/3/25
 */

@ControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorResponseDto> getResponse(APIException exception) {
        return ResponseEntity.status(exception.getCode())
                .body(new ErrorResponseDto(
                        exception.getMessage(),
                        Date.from(Instant.now()).getTime(),
                        null

        ));
    }
}
