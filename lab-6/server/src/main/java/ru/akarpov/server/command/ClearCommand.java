package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;

/**
 * Команда "clear".
 * Описание команды: очистить коллекцию.
 */
public class ClearCommand implements CommandInterface {
    CollectionManager manager;

    public ClearCommand(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     */
    @Override
    public Response execute() {
        manager.clear();
        return new Response("Коллекция очищена", " ");
    }

    @Override
    public String toString() {
        return ": очистить коллекцию";
    }
}