package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.User;
import ru.akarpov.network.Response;

public class AddCommand implements CommandWithTicketInterface {
    private final CollectionManager manager;

    public AddCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Ticket ticket, User user) {
        ticket.setUserId(user.getId());
        manager.add(ticket);
        return new Response("Элемент добавлен!", ticket.toString());
    }

    @Override
    public String toString() {
        return ": добавить новый элемент в коллекцию";
    }
}