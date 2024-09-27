package ru.akarpov.server.command;

import ru.akarpov.network.Response;

/**
 * Интерфейс команд.
 */
public interface CommandInterface {
    default Response execute() {
        throw new UnsupportedOperationException("Метод execute() не реализован");
    }
}
