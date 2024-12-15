package org.example.logisticapplication.web;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(
            String entity,
            String field,
            Object value
    ) {
        super("%s with %s=%s already exists!".formatted(entity, field, value));
    }
}
