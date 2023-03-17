package com.zengshen.rabbit.api.exception;

/**
 * @author word
 */
public class MessageException extends Exception {
    public MessageException() {
        super();
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException( Throwable cause) {
        super(cause);
    }

}
