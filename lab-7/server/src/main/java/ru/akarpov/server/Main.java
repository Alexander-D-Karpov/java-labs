package ru.akarpov.server;

import ru.akarpov.server.command.*;
import ru.akarpov.server.manager.*;
import ru.akarpov.server.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        System.setProperty("log4j.configurationFile", "resources/log4j2.xml");
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("log4j.appender.logfile.encoding", "UTF-8");

        try {
            Properties props = new Properties();
            try (InputStream input = Main.class.getClassLoader().getResourceAsStream("database.properties")) {
                if (input == null) {
                    System.err.println("Не удается найти database.properties");
                    System.exit(1);
                }
                props.load(input);
            }

            String dbUrl = props.getProperty("db.url");
            String dbUser = props.getProperty("db.user");
            String dbPassword = props.getProperty("db.password");

            if (dbUrl == null || dbUser == null || dbPassword == null) {
                System.err.println("В файле database.properties отсутствуют необходимые параметры");
                System.exit(1);
            }

            String fullDbUrl = String.format("%s?user=%s&password=%s", dbUrl, dbUser, dbPassword);

            System.out.println("Запуск сервера...");

            InetSocketAddress address = new InetSocketAddress(12727);
            CollectionManager collectionManager = new CollectionManager();
            CommandManager commandManager = new CommandManager();

            registerCommands(commandManager, collectionManager);

            ServerApp serverApp = new ServerApp(address, collectionManager, commandManager, fullDbUrl);
            serverApp.run();

            System.out.println("Сервер успешно запущен.");
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла конфигурации: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void registerCommands(CommandManager commandManager, CollectionManager collectionManager) {
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
        commandManager.addCommandWithArgs("login", new LoginCommand());
        commandManager.addCommandWithArgs("register", new RegisterCommand());
        commandManager.addCommandWithoutArgs("logout", new LogoutCommand());
        Logger.info("Команды зарегистрированы");
    }
}