package com.doesitwork.springboot.exception;

public class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7474083536172806936L;

    public EntityNotFoundException(Throwable t) {
        super(t);
    }

    public EntityNotFoundException(String message, Throwable t) {
        super(message, t);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException instance(String message) {
        throw new EntityNotFoundException(message);
    }
}
