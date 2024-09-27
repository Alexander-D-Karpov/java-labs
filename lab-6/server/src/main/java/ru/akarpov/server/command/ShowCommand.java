package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;

/**
 * Команда "show".
 * Описание команды: вывести в стандартный поток вывода все элементы коллекции в строковом представлении.
 */
public class ShowCommand implements CommandInterface {
    CollectionManager manager;

    public ShowCommand(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     */
    @Override
    public Response execute() {
        var collection = manager.getCollection();

        if (collection.isEmpty()) {
            return new Response("Коллекция пуста!", " ");
        } else {
            StringBuilder response = new StringBuilder();
            response.append("Элементы коллекции:").append("\n");
            manager.getCollection().stream().map(response::append).forEach(s -> response.append("\n"));
            return new Response(response.toString(), " ");
        }
    }

    @Override
    public String toString() {
        return ": вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }
}