package ru.akarpov.server.command;

import ru.akarpov.models.forms.TicketForm;
import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.Ticket;
import ru.akarpov.network.Response;
import ru.akarpov.server.util.Logger;

public class UpdateCommand implements CommandWithTicketAndArgsInterface {
    CollectionManager manager;

    public UpdateCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(String[] args, Ticket ticket) {
        if (args.length != 1) {
            return new Response("Команда 'update' требует один аргумент - ID билета!", "");
        }
        if (!args[0].matches("\\d+")) {
            return new Response("ID билета должен быть числом!", " ");
        }
        long id = Long.parseLong(args[0]);
        Logger.info("Получен запрос на обновление элемента с ID " + id);
        if (manager.getById(id) == null) {
            return new Response("Элемента с таким ID нет в коллекции!", " ");
        }
        if (ticket == null) {
            return new Response("Элемент с ID " + id + " найден. Введите новые данные для замены.", " ");
        } else {
            manager.update(id, ticket);
            return new Response("Элемент с ID " + id + " обновлен!", " ");
        }
    }

    @Override
    public String toString() {
        return " <id> : обновить значение элемента коллекции, ID которого равен заданному";
    }
}