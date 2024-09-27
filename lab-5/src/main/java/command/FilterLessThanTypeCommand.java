package command;

import manager.CollectionManager;
import manager.ConsoleManager;
import models.Ticket;
import models.TicketType;

public class FilterLessThanTypeCommand implements CommandInterface {
    ConsoleManager console;
    CollectionManager manager;

    public FilterLessThanTypeCommand(ConsoleManager console, CollectionManager manager) {
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
        if (args.length != 1) {
            console.printError("Команда принимает 1 аргумент!");
            return 1;
        }

        TicketType ticketType;
        try {
            ticketType = TicketType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            console.printError("Такого типа билетов нет!");
            return 1;
        }

        var response = manager.filterLessThanType(ticketType);
        if (response.isEmpty()) {
            console.println("Элементов не найдено");
        } else {
            console.println("Найденные элементы:");
            response.stream().map(Ticket::toString).forEach(console::println);
        }
        return 0;
        }

    @Override
    public String toString() {
        return " <type> : вывести элементы, значение поля type которых меньше заданного";
    }
}
