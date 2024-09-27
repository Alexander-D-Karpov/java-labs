package ru.akarpov.server.command;

import ru.akarpov.models.User;
import ru.akarpov.network.Response;

public interface CommandWithArgsInterface {
    Response execute(String[] args, User user);
}