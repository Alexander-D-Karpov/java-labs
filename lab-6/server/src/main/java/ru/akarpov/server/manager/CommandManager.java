package ru.akarpov.server.manager;

import ru.akarpov.server.command.CommandInterface;
import lombok.Getter;
import lombok.Setter;
import ru.akarpov.models.Ticket;
import ru.akarpov.network.Response;
import ru.akarpov.server.command.CommandWithArgsInterface;
import ru.akarpov.server.command.CommandWithTicketAndArgsInterface;
import ru.akarpov.server.command.CommandWithTicketInterface;
import ru.akarpov.server.util.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления командами.
 */
@Setter
@Getter
public class CommandManager {
    private HashMap<String, CommandInterface> commandsWithoutArgs = new HashMap<>();
    private HashMap<String, CommandWithArgsInterface> commandsWithArgs = new HashMap<>();
    private HashMap<String, CommandWithTicketInterface> commandsWithTicket = new HashMap<>();
    private HashMap<String, CommandWithTicketAndArgsInterface> commandsWithTicketAndArgs = new HashMap<>();

    /**
     * Добавить команду без аргументов.
     *
     * @param name    имя команды
     * @param command команда
     */
    public void addCommandWithoutArgs(String name, CommandInterface command) {
        commandsWithoutArgs.put(name, command);
        Logger.info("Зарегистрирована команда без аргументов: " + name);
    }

    /**
     * Добавить команду с аргументами.
     *
     * @param name    имя команды
     * @param command команда
     */
    public void addCommandWithArgs(String name, CommandWithArgsInterface command) {
        commandsWithArgs.put(name, command);
        Logger.info("Зарегистрирована команда с аргументами: " + name);
    }

    /**
     * Добавить команду с объектом Ticket.
     *
     * @param name    имя команды
     * @param command команда
     */
    public void addCommandWithTicket(String name, CommandWithTicketInterface command) {
        commandsWithTicket.put(name, command);
        Logger.info("Зарегистрирована команда с объектом Ticket: " + name);
    }

    public void addCommandWithTicketAndArgs(String name, CommandWithTicketAndArgsInterface command) {
        commandsWithTicketAndArgs.put(name, command);
        Logger.info("Зарегистрирована команда с объектом Ticket и аргументами: " + name);
    }

    /**
     * Выполнить команду.
     *
     * @param name имя команды
     * @param args аргументы
     * @param ticket объект Ticket
     * @return результат выполнения команды
     */
    public Response executeCommand(String name, String[] args, Ticket ticket) {
        if (commandsWithoutArgs.containsKey(name) || commandsWithArgs.containsKey(name) ||
                commandsWithTicket.containsKey(name) || commandsWithTicketAndArgs.containsKey(name)) {
            if (commandsWithoutArgs.containsKey(name)) {
                CommandInterface command = commandsWithoutArgs.get(name);
                try {
                    Logger.info("Выполнение команды без аргументов: " + name);
                    return command.execute();
                } catch (Exception e) {
                    Logger.error("Ошибка при выполнении команды без аргументов: " + name, e);
                }
            } else if (commandsWithArgs.containsKey(name)) {
                CommandWithArgsInterface command = commandsWithArgs.get(name);
                try {
                    Logger.info("Выполнение команды с аргументами: " + name);
                    if (args == null) {
                        return new Response("Команда принимает аргументы!!!!!!!!!!!", "");
                    }
                    if (args.length == 0) {
                        return new Response("Команда принимает аргументы!", "");
                    }
                    return command.execute(args);
                } catch (Exception e) {
                    Logger.error("Ошибка при выполнении команды с аргументами: " + name, e);
                }
            } else if (commandsWithTicket.containsKey(name)) {
                CommandWithTicketInterface command = commandsWithTicket.get(name);
                try {
                    Logger.info("Выполнение команды с объектом Ticket: " + name);
                    return command.execute(ticket);
                } catch (Exception e) {
                    Logger.error("Ошибка при выполнении команды с объектом Ticket: " + name, e);
                }
            } else if (commandsWithTicketAndArgs.containsKey(name)) {
                CommandWithTicketAndArgsInterface command = commandsWithTicketAndArgs.get(name);
                try {
                    Logger.info("Выполнение команды с объектом Ticket и аргументами: " + name);
                    return command.execute(args, ticket);
                } catch (Exception e) {
                    Logger.error("Ошибка при выполнении команды с объектом Ticket и аргументами: " + name, e);
                }
            } else {
                Logger.warn("Команда не найдена: " + name);
                return new Response("Ошибка: Команда '" + name + "' не найдена.", "");
            }
        }
        Logger.warn("Команда не найдена: " + name);
        return new Response("Команда '" + name + "' не найдена.", "");
    }

    public Map<String, CommandInterface> getCommands() {
        Map<String, CommandInterface> allCommands = new HashMap<>();
        allCommands.putAll(commandsWithoutArgs);
        allCommands.putAll(commandsWithArgs);
        allCommands.putAll(commandsWithTicket);
        allCommands.putAll(commandsWithTicketAndArgs);
        return allCommands;
    }

}