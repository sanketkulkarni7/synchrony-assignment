package com.synchrony.assignment.userprofile.exception;

/**
 * @author sanketku
 */
public class ImgurGatewayException extends RuntimeException {

    private int code;
    private String message;
    private String cause;

    public ImgurGatewayException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ImgurGatewayException(int code, String message, String cause) {
        this.code = code;
        this.message = message;
        this.cause = cause;
    }
}
