package command;

import manager.CollectionManager;
import manager.ConsoleManager;

/**
 * Команда "group_counting_by_type".
 * Описание команды: сгруппировать элементы коллекции по значению поля type, вывести количество элементов в каждой группе.
 */
public class GroupCountingByTypeCommand implements CommandInterface {
    ConsoleManager console;
    CollectionManager manager;

    public GroupCountingByTypeCommand(ConsoleManager console, CollectionManager manager) {
        this.console = console;
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     *
     * @param args аргументы
     */
    @Override
    public int execute(String[] args) {
        if (args.length != 0) {
            console.printError("Команда не принимает аргументы!");
            return 1;
        }
        if (manager.getCollection().isEmpty()) {
            console.printError("Коллекция пуста!");
            return 2;
        }

        console.println("Группировка по типу:");
        manager.groupCountingByType();
        return 0;
    }

    @Override
    public String toString() {
        return ": сгруппировать элементы коллекции по значению поля type, вывести количество элементов в каждой группе";
    }
}
