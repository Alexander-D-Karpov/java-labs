package command;

import manager.CollectionManager;
import manager.ConsoleManager;
import models.Event;
import models.forms.EventForm;

/**
 * Команда "filter_by_event".
 * Описание команды: вывести элементы, значение поля event которых равно заданному.
 */
public class FilterByEventCommand implements CommandInterface {
    ConsoleManager console;
    CollectionManager manager;

    public FilterByEventCommand(ConsoleManager console, CollectionManager manager) {
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

        Event event = new EventForm(console).build();
        var filteredTickets = manager.filterByEvent(event);
        if (!filteredTickets.isEmpty()) {
            console.println("Найденные элементы:");
            filteredTickets.stream().map(Object::toString).forEach(console::println);
        } else {
            console.println("Ничего не найдено! :(");
        }

        return 0;
    }

    @Override
    public String toString() {
        return " <event> : вывести элементы, значение поля event которых равно заданному";
    }
}
