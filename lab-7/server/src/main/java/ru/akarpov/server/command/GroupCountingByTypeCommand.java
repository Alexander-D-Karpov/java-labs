package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;
import ru.akarpov.models.User;

public class GroupCountingByTypeCommand implements CommandInterface {
    CollectionManager manager;

    public GroupCountingByTypeCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(User user) {
        if (manager.getCollection().isEmpty()) {
            return new Response("Коллекция пуста!", "");
        }

        return new Response("Группировка по типу: " + manager.groupCountingByType(), "");
    }

    @Override
    public String toString() {
        return ": сгруппировать элементы коллекции по значению поля type, вывести количество элементов в каждой группе";
    }
}