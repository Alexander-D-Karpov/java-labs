package ru.akarpov.server.command;

import ru.akarpov.network.Response;

public interface CommandWithArgsInterface extends CommandInterface {
    Response execute(String[] args);
}