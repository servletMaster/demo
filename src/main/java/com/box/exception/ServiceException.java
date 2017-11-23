package com.box.exception;

/**
 * 类说明 service层异常
 */

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 415125440663762586L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorCode errorCode;

    public ServiceException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
