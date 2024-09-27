package ru.akarpov.server.command;

import ru.akarpov.server.manager.CommandManager;
import ru.akarpov.network.Response;
import ru.akarpov.models.User;

public class HelpCommand implements CommandInterface {
    CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(User user) {
        StringBuilder help = new StringBuilder("Доступные команды:\n");
        manager.getCommands().forEach((name, type) ->
                help.append(name).append(" - ").append(type).append("\n"));
        return new Response(help.toString(), "");
    }

    @Override
    public String toString() {
        return ": вывести справку по доступным командам";
    }
}