package ru.akarpov.server.command;

import ru.akarpov.models.Ticket;
import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;
import ru.akarpov.models.User;

public class RemoveHeadCommand implements CommandInterface {
    CollectionManager manager;

    public RemoveHeadCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(User user) {
        Ticket firstTicket = manager.removeHead(user.getId());
        if (firstTicket == null) {
            return new Response("Коллекция пуста или у вас нет прав на удаление первого элемента", "");
        }

        return new Response("Удален первый элемент коллекции: " + firstTicket, "");
    }

    @Override
    public String toString() {
        return ": вывести первый элемент коллекции и удалить его";
    }
}