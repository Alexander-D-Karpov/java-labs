package command;

import manager.CollectionManager;
import manager.ConsoleManager;
import models.Ticket;

/**
 * Команда "remove_head".
 * Описание команды: вывести первый элемент коллекции и удалить его.
 */
public class RemoveHeadCommand implements CommandInterface {
    ConsoleManager console;
    CollectionManager manager;

    public RemoveHeadCommand(ConsoleManager console, CollectionManager manager) {
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

        Ticket firstTicket = manager.removeHead();
        if (firstTicket == null) {
            console.printError("Коллекция пуста");
            return 2;
        }

        console.println("Первый элемент коллекции: " + firstTicket);
        return 0;
    }

    @Override
    public String toString() {
        return ": вывести первый элемент коллекции и удалить его";
    }

}
