package com.doesitwork.springboot.exception;

public class UnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 8741748927225268038L;

    public UnauthorizedException() {
        super("service_exception");
    }

    public UnauthorizedException(Throwable t) {
        super(t);
    }

    public UnauthorizedException(String message, Throwable t) {
        super(message, t);
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public static UnauthorizedException instance(String message) {
        return new UnauthorizedException(message);
    }
}
