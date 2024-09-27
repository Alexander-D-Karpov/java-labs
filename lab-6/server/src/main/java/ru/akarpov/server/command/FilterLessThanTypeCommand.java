package ru.akarpov.server.command;

import ru.akarpov.server.manager.CollectionManager;
import ru.akarpov.models.TicketType;
import ru.akarpov.network.Response;

public class FilterLessThanTypeCommand implements CommandWithArgsInterface {
    CollectionManager manager;

    public FilterLessThanTypeCommand(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Выполнение команды.
     *
     * @param args аргументы
     */
    @Override
    public Response execute(String[] args) {
        if (args.length != 1) {
            return new Response("Команда принимает 1 аргумент!", "");
        }

        TicketType ticketType;
        try {
            ticketType = TicketType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            return new Response("Такого типа билетов нет!", "");
        }

        var response = manager.filterLessThanType(ticketType);
        if (response.isEmpty()) {
            return new Response("Элементов не найдено", "");
        } else {
            return new Response("Найденные элементы:", response.toString());
        }
    }

    @Override
    public String toString() {
        return " <type> : вывести элементы, значение поля type которых меньше заданного";
    }
}