package ru.akarpov.client.util;

import java.util.HashMap;
import java.util.Map;

public class ClientCommandManager {
    private final Map<String, CommandType> commands = new HashMap<>();

    public void registerCommand(String commandName, CommandType commandType) {
        commands.put(commandName, commandType);
    }

    public boolean isValidCommand(String commandName) {
        return commands.containsKey(commandName);
    }

    public CommandType getCommandType(String commandName) {
        return commands.get(commandName);
    }

    public void printHelpMessage() {
        System.out.println("Доступные команды:");
        commands.forEach((name, type) -> System.out.println(name + " - " + type));
    }

    public enum CommandType {
        WITHOUT_ARGS,
        WITH_ARGS,
        WITH_TICKET
    }
}
