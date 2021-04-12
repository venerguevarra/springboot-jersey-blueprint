package com.doesitwork.springboot.exception;

public class EntityDeactivatedException extends RuntimeException {

    private static final long serialVersionUID = -7474083536172806936L;

    public EntityDeactivatedException() {
        super("entity_conflict_exception");
    }

    public EntityDeactivatedException(Throwable t) {
        super(t);
    }

    public EntityDeactivatedException(String message, Throwable t) {
        super(message, t);
    }

    public EntityDeactivatedException(String message) {
        super(message);
    }

    public static EntityDeactivatedException instance() {
        return new EntityDeactivatedException();
    }

    public static EntityDeactivatedException instance(String message) {
        return new EntityDeactivatedException(message);
    }
}
