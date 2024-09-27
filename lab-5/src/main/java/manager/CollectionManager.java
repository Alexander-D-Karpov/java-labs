package manager;

import lombok.Getter;
import lombok.Setter;
import models.Event;
import models.Ticket;
import models.TicketType;

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
        collection.addLast(ticket);
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
                });
    }

    public void removeById(long id) {
        collection.removeIf(ticket -> ticket.getId() == id);
    }

    public void clear() {
        initializationTime = new Date();
        collection.clear();
    }

    public void addIfMin(Ticket ticket) {
        if (collection.stream().min(Ticket::compareTo).orElse(ticket).compareTo(ticket) >= 0) {
            collection.addLast(ticket);
        }
    }

    public List<Ticket> filterByEvent(Event event) {
        return collection.stream()
                .filter(ticket -> ticket.getEvent().equals(event))
                .toList();
    }

    public List<Ticket> filterLessThanType(TicketType type) {
        return collection.stream()
                .filter(ticket -> ticket.getType().getStatus() < type.getStatus())
                .toList();
    }

    public int removeGreater(Ticket ticket) {
        int initialSize = collection.size();
        collection.removeIf(t -> t.compareTo(ticket) > 0);
        return initialSize - collection.size();  // Return the count of removed tickets
    }

    public void groupCountingByType() {
        collection.stream()
                .collect(Collectors.groupingBy(Ticket::getType, Collectors.counting()))
                .forEach((type, count) -> System.out.println(type + ": " + count));
    }

    public Ticket removeHead() {
        return collection.pollFirst();
    }

    public Ticket getById(long id) {
        return collection.stream()
                .filter(ticket -> ticket.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Event> getEvents() {
        return collection.stream()
                .map(Ticket::getEvent)
                .toList();
    }
}