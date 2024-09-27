package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;
import ru.akarpov.models.User;

public class ShowCommand implements CommandInterface {
    CollectionManager manager;

    public ShowCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(User user) {
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
        return ": вывести все элементы коллекции";
    }
}