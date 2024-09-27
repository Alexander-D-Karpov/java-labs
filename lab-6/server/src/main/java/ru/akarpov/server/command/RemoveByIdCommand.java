package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;

/**
 * Команда "remove_by_id".
 * Описание команды: удалить элемент из коллекции по его id.
 */
public class RemoveByIdCommand implements CommandWithArgsInterface {
    CollectionManager manager;

    public RemoveByIdCommand(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     *
     * @param args аргументы
     */
    @Override
    public Response execute(String[] args) {
        if (args.length != 1) {
            return new Response("Команда принимает один аргумент!", " ");
        }

        try {
            long id = Long.parseLong(args[0]);
            if (manager.getById(id) == null) {
                return new Response("Элемент с id " + id + " не найден", " ");
            }

            manager.removeById(id);
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