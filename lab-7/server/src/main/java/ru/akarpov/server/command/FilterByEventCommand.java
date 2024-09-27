package ru.akarpov.server.command;

import ru.akarpov.models.Ticket;
import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.Event;
import ru.akarpov.models.User;
import ru.akarpov.network.Response;
import java.util.List;

public class FilterByEventCommand implements CommandWithTicketInterface {
    private final CollectionManager manager;

    public FilterByEventCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Ticket ticket, User user) {
        if (ticket == null || ticket.getEvent() == null) {
            return new Response("Ошибка: событие не предоставлено", "", null, null, false);
        }
        Event event = ticket.getEvent();
        List<Ticket> filteredTickets = manager.filterByEvent(event);
        if (!filteredTickets.isEmpty()) {
            return new Response("Найденные элементы:", "", filteredTickets, null, true);
        } else {
            return new Response("Элементы с таким событием не найдены", "", null, null, false);
        }
    }

    @Override
    public String toString() {
        return ": вывести элементы, значение поля event которых равно заданному";
    }
}