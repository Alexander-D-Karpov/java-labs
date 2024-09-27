package ru.akarpov.server.command;

import ru.akarpov.models.Ticket;
import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.Event;
import ru.akarpov.network.Response;
import java.util.List;

/**
 * Команда "filter_by_event".
 * Описание команды: вывести элементы, значение поля event которых равно заданному.
 */
public class FilterByEventCommand implements CommandWithTicketInterface {
    private final CollectionManager manager;

    public FilterByEventCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Ticket ticket) {
        if (ticket == null || ticket.getEvent() == null) {
            return new Response("Ошибка: событие не предоставлено", "", null);
        }
        Event event = ticket.getEvent();
        List<Ticket> filteredTickets = manager.filterByEvent(event);
        if (!filteredTickets.isEmpty()) {
            return new Response("Найденные элементы:", "", filteredTickets);
        } else {
            return new Response("Элементы с таким event не найдены", "", null);
        }
    }

    @Override
    public String toString() {
        return ": вывести элементы, значение поля event которых равно заданному";
    }
}