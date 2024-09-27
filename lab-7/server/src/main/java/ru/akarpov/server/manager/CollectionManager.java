package ru.akarpov.server.manager;

import lombok.Getter;
import lombok.Setter;
import ru.akarpov.models.Event;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.TicketType;
import ru.akarpov.server.util.Logger;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Getter
@Setter
public class CollectionManager {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private ArrayDeque<Ticket> collection = new ArrayDeque<>();
    public static Date initializationTime = new Date();

    public void loadCollection() {
        lock.writeLock().lock();
        try {
            collection.clear();
            collection.addAll(DatabaseManager.getAllTickets());
            Logger.info("Коллекция загружена из базы данных");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void add(Ticket ticket) {
        if (DatabaseManager.addTicket(ticket)) {
            lock.writeLock().lock();
            try {
                collection.addLast(ticket);
                Logger.info("Билет добавлен в коллекцию: " + ticket);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            Logger.warn("Не удалось добавить билет в базу данных: " + ticket);
        }
    }

    public void update(long id, Ticket newTicket, int userId) {
        lock.writeLock().lock();
        try {
            Ticket existingTicket = getById(id);
            if (existingTicket != null && existingTicket.getUserId() == userId) {
                if (DatabaseManager.updateTicket(newTicket)) {
                    collection.removeIf(ticket -> ticket.getId() == id);
                    collection.addLast(newTicket);
                    Logger.info("Билет с id " + id + " обновлен: " + newTicket);
                } else {
                    Logger.warn("Не удалось обновить билет в базе данных: " + newTicket);
                }
            } else {
                Logger.warn("Обновление не удалось: билет не найден или у пользователя нет прав");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeById(long id, int userId) {
        lock.writeLock().lock();
        try {
            Ticket ticket = getById(id);
            if (ticket != null && ticket.getUserId() == userId) {
                if (DatabaseManager.removeTicket(id, userId)) {
                    collection.removeIf(t -> t.getId() == id);
                    Logger.info("Билет с id " + id + " удален из коллекции");
                } else {
                    Logger.warn("Не удалось удалить билет с id " + id + " из базы данных");
                }
            } else {
                Logger.warn("Удаление не удалось: билет не найден или у пользователя нет прав");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear(int userId) {
        lock.writeLock().lock();
        try {
            if (DatabaseManager.clearUserTickets(userId)) {
                collection.removeIf(ticket -> ticket.getUserId() == userId);
                Logger.info("Коллекция очищена для пользователя " + userId);
            } else {
                Logger.warn("Не удалось очистить коллекцию для пользователя " + userId);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addIfMin(Ticket ticket) {
        lock.writeLock().lock();
        try {
            if (collection.stream().min(Ticket::compareTo).orElse(ticket).compareTo(ticket) > 0) {
                if (DatabaseManager.addTicket(ticket)) {
                    collection.addLast(ticket);
                    Logger.info("Билет добавлен в коллекцию как минимальный: " + ticket);
                } else {
                    Logger.warn("Не удалось добавить минимальный билет в базу данных: " + ticket);
                }
            } else {
                Logger.info("Билет не добавлен: не является минимальным");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Ticket removeHead(int userId) {
        lock.writeLock().lock();
        try {
            Ticket head = collection.peek();
            if (head != null && head.getUserId() == userId) {
                if (DatabaseManager.removeTicket(head.getId(), userId)) {
                    return collection.pollFirst();
                } else {
                    Logger.warn("Не удалось удалить первый билет из базы данных");
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Ticket getById(long id) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(ticket -> ticket.getId() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Ticket> filterByEvent(Event event) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(ticket -> ticket.getEvent().equals(event))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Ticket> filterLessThanType(TicketType type) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(ticket -> ticket.getType().getStatus() < type.getStatus())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public void removeGreater(Ticket ticket, int userId) {
        lock.writeLock().lock();
        try {
            List<Ticket> toRemove = collection.stream()
                    .filter(t -> t.getUserId() == userId && t.compareTo(ticket) > 0)
                    .toList();

            for (Ticket t : toRemove) {
                if (DatabaseManager.removeTicket(t.getId(), userId)) {
                    collection.remove(t);
                } else {
                    Logger.warn("Не удалось удалить билет " + t.getId() + " из базы данных");
                }
            }

            Logger.info("Удалено " + toRemove.size() + " билетов, превышающих заданный");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String groupCountingByType() {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .collect(Collectors.groupingBy(Ticket::getType, Collectors.counting()))
                    .entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining("\n"));
        } finally {
            lock.readLock().unlock();
        }
    }

    public Event getEventById(long id) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .map(Ticket::getEvent)
                    .filter(event -> event.getId() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Ticket> getUserTickets(int userId) {
        lock.readLock().lock();
        try {
            return collection.stream()
                    .filter(ticket -> ticket.getUserId() == userId)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean userOwnsTicket(long ticketId, int userId) {
        lock.readLock().lock();
        try {
            Ticket ticket = getById(ticketId);
            return ticket != null && ticket.getUserId() == userId;
        } finally {
            lock.readLock().unlock();
        }
    }
}