package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.User;
import ru.akarpov.network.Response;

public class UpdateCommand implements CommandWithTicketAndArgsInterface {
    CollectionManager manager;

    public UpdateCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(String[] args, Ticket ticket, User user) {
        if (args.length != 1) {
            return new Response("Команда 'update' требует один аргумент - ID билета!", "", false);
        }
        try {
            long id = Long.parseLong(args[0]);
            Ticket existingTicket = manager.getById(id);
            if (existingTicket == null) {
                return new Response("Элемента с таким ID нет в коллекции!", "", false);
            }
            if (existingTicket.getUserId() != user.getId()) {
                return new Response("У вас нет прав на обновление этого билета!", "", false);
            }
            if (ticket == null) {
                return new Response("Элемент с ID " + id + " найден. Пожалуйста, введите новые данные для обновления.", "", true);
            } else {
                ticket.setId(id);
                ticket.setUserId(user.getId());
                manager.update(id, ticket, user.getId());
                return new Response("Элемент с ID " + id + " обновлен!", "", true);
            }
        } catch (NumberFormatException e) {
            return new Response("ID билета должен быть числом!", "", false);
        }
    }


    @Override
    public String toString() {
        return " <id> : обновить значение элемента коллекции, ID которого равен заданному";
    }
}