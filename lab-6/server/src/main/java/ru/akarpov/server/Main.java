package ru.akarpov.server;

import ru.akarpov.server.command.*;
import ru.akarpov.server.manager.*;
import ru.akarpov.server.util.Logger;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        System.setProperty("log4j.configurationFile", "resources/log4j2.xml");
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("log4j.appender.logfile.encoding", "UTF-8");

        try {
            System.out.println("Запуск сервера...");

            InetSocketAddress address = new InetSocketAddress(12727);
            CollectionManager collectionManager = new CollectionManager();
            CommandManager commandManager = new CommandManager();
            FileManager fileManager = new FileManager("data.xml", collectionManager);

            registerCommands(commandManager, collectionManager, fileManager);

            ServerApp serverApp = new ServerApp(address, collectionManager, commandManager, fileManager);
            serverApp.run(args);

            System.out.println("Сервер успешно запущен.");
        } catch (Exception e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void registerCommands(CommandManager commandManager, CollectionManager collectionManager, FileManager fileManager) {
        commandManager.addCommandWithoutArgs("help", new HelpCommand(commandManager));
        commandManager.addCommandWithoutArgs("info", new InfoCommand(collectionManager));
        commandManager.addCommandWithoutArgs("show", new ShowCommand(collectionManager));
        commandManager.addCommandWithTicket("add", new AddCommand(collectionManager));
        commandManager.addCommandWithTicketAndArgs("update", new UpdateCommand(collectionManager));
        commandManager.addCommandWithArgs("remove_by_id", new RemoveByIdCommand(collectionManager));
        commandManager.addCommandWithoutArgs("clear", new ClearCommand(collectionManager));
        commandManager.addCommandWithoutArgs("remove_head", new RemoveHeadCommand(collectionManager));
        commandManager.addCommandWithTicket("add_if_min", new AddIfMinCommand(collectionManager));
        commandManager.addCommandWithoutArgs("group_counting_by_type", new GroupCountingByTypeCommand(collectionManager));
        commandManager.addCommandWithTicket("filter_by_event", new FilterByEventCommand(collectionManager));
        commandManager.addCommandWithoutArgs("save", new SaveCommand(fileManager));
        Logger.info("Команды зарегистрированы");
    }
}