package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.Ticket;
import ru.akarpov.network.Response;

/**
 * Команда "remove_greater".
 * Описание команды: удалить из коллекции все элементы, превышающие заданный.
 */
public class RemoveGreaterCommand implements CommandWithTicketInterface {
    CollectionManager manager;

    public RemoveGreaterCommand(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     *
     * @param ticket объект класса Ticket
     */
    @Override
    public Response execute(Ticket ticket) {
        var size = manager.getCollection().size();
        manager.removeGreater(ticket);
        return new Response("Удалено " + (size - manager.getCollection().size()) + " элементов, превышающих заданный", " ");
    }

    @Override
    public String toString() {
        return ": удалить из коллекции все элементы, превышающие заданный";
    }
}