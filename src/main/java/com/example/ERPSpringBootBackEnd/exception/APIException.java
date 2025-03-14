package com.example.ERPSpringBootBackEnd.exception;

import lombok.Data;

/**
 * @author Rimi
 * @since 10/3/25
 */

@Data
public class APIException extends RuntimeException{
    private int code;

    public APIException(String message, int code) {
        super(message);
        this.code = code;
    }
}
