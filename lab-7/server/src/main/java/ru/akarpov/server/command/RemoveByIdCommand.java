package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;
import ru.akarpov.models.User;

public class RemoveByIdCommand implements CommandWithArgsInterface {
    CollectionManager manager;

    public RemoveByIdCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(String[] args, User user) {
        if (args.length != 1) {
            return new Response("Команда принимает один аргумент!", " ");
        }

        try {
            long id = Long.parseLong(args[0]);
            if (manager.getById(id) == null) {
                return new Response("Элемент с id " + id + " не найден", " ");
            }

            if (!manager.userOwnsTicket(id, user.getId())) {
                return new Response("У вас нет прав на удаление этого элемента", " ");
            }

            manager.removeById(id, user.getId());
            return new Response("Элемент с id " + id + " удален", " ");
        } catch (NumberFormatException e) {
            return new Response("Неверный формат аргумента!", " ");
        }
    }

    @Override
    public String toString() {
        return " <id> : удалить элемент из коллекции по его id";
    }
}