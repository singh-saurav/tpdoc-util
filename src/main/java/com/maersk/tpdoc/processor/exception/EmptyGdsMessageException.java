package com.maersk.tpdoc.processor.exception;

public class EmptyGdsMessageException extends RuntimeException {

    public EmptyGdsMessageException(String message) {
        super(message);
    }
}
