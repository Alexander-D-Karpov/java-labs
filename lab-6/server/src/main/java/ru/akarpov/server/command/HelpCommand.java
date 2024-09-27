package ru.akarpov.server.command;

import ru.akarpov.server.manager.CommandManager;
import ru.akarpov.network.Response;

/**
 * Команда "help".
 * Описание команды: вывести справку по доступным командам.
 */
public class HelpCommand implements CommandInterface {
    CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute() {
        StringBuilder help = new StringBuilder();

        help.append("Доступные команды:\n");
        manager.getCommands().forEach((name, command) -> help.append(name).append(command.toString()).append('\n'));
        return new Response(help.toString(), "");
    }

    @Override
    public String toString() {
        return ": вывести справку по доступным командам";
    }
}
