package ru.akarpov.server.command;

import ru.akarpov.models.Ticket;
import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;

/**
 * Команда "remove_head".
 * Описание команды: вывести первый элемент коллекции и удалить его.
 */
public class RemoveHeadCommand implements CommandInterface {
    CollectionManager manager;

    public RemoveHeadCommand(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     */
    @Override
    public Response execute() {
        Ticket firstTicket = manager.removeHead();
        if (firstTicket == null) {
            return new Response("Коллекция пуста", "");
        }

        return new Response("Удален первый элемент коллекции: " + firstTicket, "");
    }

    @Override
    public String toString() {
        return ": вывести первый элемент коллекции и удалить его";
    }
}