package ru.akarpov.server.manager;

import ru.akarpov.server.command.*;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.User;
import ru.akarpov.network.Response;
import ru.akarpov.server.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final HashMap<String, CommandInterface> commandsWithoutArgs = new HashMap<>();
    private final HashMap<String, CommandWithArgsInterface> commandsWithArgs = new HashMap<>();
    private final HashMap<String, CommandWithTicketInterface> commandsWithTicket = new HashMap<>();
    private final HashMap<String, CommandWithTicketAndArgsInterface> commandsWithTicketAndArgs = new HashMap<>();

    public void addCommandWithoutArgs(String name, CommandInterface command) {
        commandsWithoutArgs.put(name, command);
        Logger.info("Зарегистрирована команда без аргументов: " + name);
    }

    public void addCommandWithArgs(String name, CommandWithArgsInterface command) {
        commandsWithArgs.put(name, command);
        Logger.info("Зарегистрирована команда с аргументами: " + name);
    }

    public void addCommandWithTicket(String name, CommandWithTicketInterface command) {
        commandsWithTicket.put(name, command);
        Logger.info("Зарегистрирована команда с объектом Ticket: " + name);
    }

    public void addCommandWithTicketAndArgs(String name, CommandWithTicketAndArgsInterface command) {
        commandsWithTicketAndArgs.put(name, command);
        Logger.info("Зарегистрирована команда с объектом Ticket и аргументами: " + name);
    }

    public Response executeCommand(String name, String[] args, Ticket ticket, User user) {
        if (user == null && !name.equals("login") && !name.equals("register")) {
            return new Response("Необходима аутентификация. Пожалуйста, войдите в систему или зарегистрируйтесь.", "");
        }

        if (commandsWithoutArgs.containsKey(name)) {
            CommandInterface command = commandsWithoutArgs.get(name);
            try {
                Logger.info("Выполнение команды без аргументов: " + name);
                return command.execute(user);
            } catch (Exception e) {
                Logger.error("Ошибка при выполнении команды без аргументов: " + name, e);
            }
        } else if (commandsWithArgs.containsKey(name)) {
            CommandWithArgsInterface command = commandsWithArgs.get(name);
            try {
                Logger.info("Выполнение команды с аргументами: " + name);
                if (args == null || args.length == 0) {
                    return new Response("Команда принимает аргументы!", "");
                }
                return command.execute(args, user);
            } catch (Exception e) {
                Logger.error("Ошибка при выполнении команды с аргументами: " + name, e);
            }
        } else if (commandsWithTicket.containsKey(name)) {
            CommandWithTicketInterface command = commandsWithTicket.get(name);
            try {
                Logger.info("Выполнение команды с объектом Ticket: " + name);
                return command.execute(ticket, user);
            } catch (Exception e) {
                Logger.error("Ошибка при выполнении команды с объектом Ticket: " + name, e);
            }
        } else if (commandsWithTicketAndArgs.containsKey(name)) {
            CommandWithTicketAndArgsInterface command = commandsWithTicketAndArgs.get(name);
            try {
                Logger.info("Выполнение команды с объектом Ticket и аргументами: " + name);
                return command.execute(args, ticket, user);
            } catch (Exception e) {
                Logger.error("Ошибка при выполнении команды с объектом Ticket и аргументами: " + name, e);
            }
        }

        Logger.warn("Команда не найдена: " + name);
        return new Response("Команда '" + name + "' не найдена.", "");
    }

    public Map<String, String> getCommands() {
        Map<String, String> allCommands = new HashMap<>();
        commandsWithoutArgs.forEach((name, command) -> allCommands.put(name, "Команда без аргументов"));
        commandsWithArgs.forEach((name, command) -> allCommands.put(name, "Команда с аргументами"));
        commandsWithTicket.forEach((name, command) -> allCommands.put(name, "Команда с объектом Ticket"));
        commandsWithTicketAndArgs.forEach((name, command) -> allCommands.put(name, "Команда с объектом Ticket и аргументами"));
        return allCommands;
    }
}