package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.server.manager.IdManager;
import ru.akarpov.models.Ticket;
import ru.akarpov.network.Response;

/**
 * Команда "add".
 * Описание команды: добавить новый элемент в коллекцию.
 */
public class AddCommand implements CommandWithTicketInterface {
    CollectionManager manager;

    public AddCommand(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     *
     * @param ticket объект класса Ticket
     */
    @Override
    public Response execute(Ticket ticket) {
        if (ticket.getId() == -1) {
            ticket.setId(IdManager.generateId());
        }
        if (ticket.getEvent().getId() == -1) {
            ticket.getEvent().setId(IdManager.generateEventId());
        }
        manager.add(ticket);
        return new Response("Элемент добавлен!", ticket.toString());
    }

    @Override
    public String toString() {
        return ": добавить новый элемент в коллекцию";
    }
}
