package ru.akarpov.server.manager;

import lombok.Getter;
import lombok.Setter;
import ru.akarpov.models.Event;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.TicketType;
import ru.akarpov.server.util.Logger;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CollectionManager {
    public static Date initializationTime = new Date();
    private ArrayDeque<Ticket> collection = new ArrayDeque<>();

    public void add(Ticket ticket) {
        if (ticket.getId() == -1) {
            ticket.setId(IdManager.generateId());
        }
        if (ticket.getEvent().getId() == -1) {
            ticket.getEvent().setId(IdManager.generateEventId());
        }
        collection.addLast(ticket);
        Logger.info("Элемент добавлен в коллекцию: " + ticket);
    }

    public void update(long id, Ticket newTicket) {
        collection.stream()
                .filter(ticket -> ticket.getId() == id)
                .forEach(ticket -> {
                    ticket.setName(newTicket.getName());
                    ticket.setCoordinates(newTicket.getCoordinates());
                    ticket.setCreationDate(newTicket.getCreationDate());
                    ticket.setPrice(newTicket.getPrice());
                    ticket.setDiscount(newTicket.getDiscount());
                    ticket.setType(newTicket.getType());
                    ticket.setEvent(newTicket.getEvent());
                    Logger.info("Элемент с id " + id + " обновлен: " + newTicket);
                });
    }

    public void removeById(long id) {
        collection.removeIf(ticket -> ticket.getId() == id);
        Logger.info("Элемент с id " + id + " удален из коллекции");
    }

    public void clear() {
        initializationTime = new Date();
        collection.clear();
        Logger.info("Коллекция очищена");
    }

    public void addIfMin(Ticket ticket) {
        if (collection.stream().min(Ticket::compareTo).orElse(ticket).compareTo(ticket) >= 0) {
            if (ticket.getId() == -1) {
                ticket.setId(IdManager.generateId());
            }
            if (ticket.getEvent().getId() == -1) {
                ticket.getEvent().setId(IdManager.generateEventId());
            }
            collection.addLast(ticket);
            Logger.info("Элемент добавлен в коллекцию: " + ticket);
        }
    }

    public Ticket removeHead() {
        Ticket ticket = collection.pollFirst();
        if (ticket != null) {
            Logger.info("Удален первый элемент коллекции: " + ticket);
        }
        return ticket;
    }

    public Ticket getById(long id) {
        return collection.stream()
                .filter(ticket -> ticket.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Ticket> filterByEvent(Event event) {
        List<Ticket> filteredTickets = collection.stream()
                .filter(ticket -> ticket.getEvent().equals(event))
                .collect(Collectors.toList());
        Logger.info("Найдено " + filteredTickets.size() + " элементов с событием: " + event);
        return filteredTickets;
    }

    public List<Ticket> filterLessThanType(TicketType type) {
        List<Ticket> filteredTickets = collection.stream()
                .filter(ticket -> ticket.getType().getStatus() < type.getStatus())
                .toList();
        Logger.info("Найдено " + filteredTickets.size() + " элементов с типом меньше " + type);
        return filteredTickets;
    }

    public void removeGreater(Ticket ticket) {
        boolean removedCount = collection.removeIf(t -> t.compareTo(ticket) > 0);
        Logger.info("Удалено " + removedCount + " элементов, превышающих заданный");
    }

    public String groupCountingByType() {
        return collection.stream()
                .collect(Collectors.groupingBy(Ticket::getType, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    public Event getEventById(long id) {
        return getCollection().stream()
                .map(Ticket::getEvent)
                .filter(event -> event.getId() == id)
                .findFirst()
                .orElse(null);
    }
}