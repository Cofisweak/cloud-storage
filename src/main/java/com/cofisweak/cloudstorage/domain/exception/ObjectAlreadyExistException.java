package com.cofisweak.cloudstorage.domain.exception;

public class ObjectAlreadyExistException extends RuntimeException {

    public ObjectAlreadyExistException(String message) {
        super(message);
    }

}
