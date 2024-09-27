package ru.akarpov.server.command;

import ru.akarpov.models.User;
import ru.akarpov.network.Response;

/**
 * Интерфейс команд.
 */
public interface CommandInterface {
    Response execute(User user);
}
