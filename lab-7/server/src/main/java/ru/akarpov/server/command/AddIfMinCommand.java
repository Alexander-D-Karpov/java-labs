package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.User;
import ru.akarpov.network.Response;

public class AddIfMinCommand implements CommandWithTicketInterface {
    CollectionManager manager;

    public AddIfMinCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Ticket ticket, User user) {
        ticket.setUserId(user.getId());
        var size = manager.getCollection().size();
        manager.addIfMin(ticket);
        if (size == manager.getCollection().size())
            return new Response("Элемент не добавлен, так как не является минимальным", " ");
        else {
            return new Response("Элемент добавлен как минимальный!", " ");
        }
    }

    @Override
    public String toString() {
        return ": добавить новый элемент, если его значение меньше минимального";
    }
}