package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.User;
import ru.akarpov.network.Response;

public class RemoveGreaterCommand implements CommandWithTicketInterface {
    CollectionManager manager;

    public RemoveGreaterCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Ticket ticket, User user) {
        var size = manager.getCollection().size();
        manager.removeGreater(ticket, user.getId());
        return new Response("Удалено " + (size - manager.getCollection().size()) + " элементов, превышающих заданный", " ");
    }

    @Override
    public String toString() {
        return ": удалить из коллекции все элементы, превышающие заданный";
    }
}