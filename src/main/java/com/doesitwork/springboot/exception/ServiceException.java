package com.doesitwork.springboot.exception;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 8741748927225268038L;

    public ServiceException() {
        super("service_exception");
    }

    public ServiceException(Throwable t) {
        super(t);
    }

    public ServiceException(String message, Throwable t) {
        super(message, t);
    }

    public ServiceException(String message) {
        super(message);
    }

    public static ServiceException instance(String message) {
        return new ServiceException(message);
    }
}
