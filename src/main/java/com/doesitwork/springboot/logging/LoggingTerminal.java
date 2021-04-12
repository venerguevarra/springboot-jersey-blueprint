package com.doesitwork.springboot.logging;

public interface LoggingTerminal {
    void log();
    void log(Object actorOrLogger);
}
