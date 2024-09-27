package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;
import ru.akarpov.models.User;

public class ClearCommand implements CommandInterface {
    CollectionManager manager;

    public ClearCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(User user) {
        manager.clear(user.getId());
        return new Response("Коллекция очищена для пользователя " + user.getUsername(), " ");
    }

    @Override
    public String toString() {
        return ": очистить коллекцию";
    }
}