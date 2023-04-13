package com.marketplace.vintage.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String exception) {
        super("Entity already exists: " + exception);
    }
}
