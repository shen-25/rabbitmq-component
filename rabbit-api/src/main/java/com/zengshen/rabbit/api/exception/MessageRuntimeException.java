package com.zengshen.rabbit.api.exception;

/**
 * @author word
 */
public class MessageRuntimeException extends RuntimeException {
    public MessageRuntimeException() {
        super();
    }

    public MessageRuntimeException(String message) {
        super(message);
    }

    public MessageRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageRuntimeException(Throwable cause) {
        super(cause);
    }

}
