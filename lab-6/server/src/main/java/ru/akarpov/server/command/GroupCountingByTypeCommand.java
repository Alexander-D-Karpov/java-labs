package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.network.Response;

/**
 * Команда "group_counting_by_type".
 * Описание команды: сгруппировать элементы коллекции по значению поля type, вывести количество элементов в каждой группе.
 */
public class GroupCountingByTypeCommand implements CommandInterface {
    CollectionManager manager;

    public GroupCountingByTypeCommand(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     */
    @Override
    public Response execute() {
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