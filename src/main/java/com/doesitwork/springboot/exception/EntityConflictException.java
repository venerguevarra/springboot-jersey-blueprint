package com.doesitwork.springboot.exception;

public class EntityConflictException extends RuntimeException {

    private static final long serialVersionUID = -7474083536172806936L;

    public EntityConflictException() {
        super("entity_conflict_exception");
    }

    public EntityConflictException(Throwable t) {
        super(t);
    }

    public EntityConflictException(String message, Throwable t) {
        super(message, t);
    }

    public EntityConflictException(String message) {
        super(message);
    }

    public static EntityConflictException instance() {
        return new EntityConflictException();
    }

    public static EntityConflictException instance(String message) {
        return new EntityConflictException(message);
    }
}
