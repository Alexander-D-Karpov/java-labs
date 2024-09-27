package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.Ticket;
import ru.akarpov.network.Response;

/**
 * Команда "add_if_min".
 * Описание команды: добавить новый элемент, если его значение меньше, чем у наименьшего элемента коллекции.
 */
public class AddIfMinCommand implements CommandWithTicketInterface {
    CollectionManager manager;

    public AddIfMinCommand(CollectionManager manager) {
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
        manager.addIfMin(ticket);
        if (size == manager.getCollection().size())
            return new Response("Элемент не добавлен (", " ");
        else {
            return new Response("Элемент добавлен!", " ");
        }
    }

    @Override
    public String toString() {
        return ": добавить новый элемент, если его значение меньше, чем у наименьшего элемента коллекции";
    }
}