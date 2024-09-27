package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;

/**
 * Команда "info".
 * Описание команды: вывести в стандартный поток вывода информацию о коллекции.
 */
public class InfoCommand implements CommandInterface {
    CollectionManager manager;

    public InfoCommand(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     */
    @Override
    public Response execute() {
        String response = "Дата инициализации коллекции: " + CollectionManager.initializationTime + "\n" +
                "Тип коллекции: " + manager.getCollection().getClass().getName() + "\n" +
                "Размер коллекции: " + manager.getCollection().size();
        return new Response(response, " ");
    }

    @Override
    public String toString() {
        return ": вывести в стандартный поток вывода информацию о коллекции";
    }
}