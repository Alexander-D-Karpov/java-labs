package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;
import ru.akarpov.models.User;

public class InfoCommand implements CommandInterface {
    CollectionManager manager;

    public InfoCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(User user) {
        String response = "Дата инициализации коллекции: " + CollectionManager.initializationTime + "\n" +
                "Тип коллекции: " + manager.getCollection().getClass().getName() + "\n" +
                "Размер коллекции: " + manager.getCollection().size();
        return new Response(response, " ");
    }

    @Override
    public String toString() {
        return ": вывести информацию о коллекции";
    }
}