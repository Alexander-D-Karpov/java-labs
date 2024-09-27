package ru.akarpov.client;

import ru.akarpov.client.util.Client;
import ru.akarpov.client.util.ExecuteScriptCommand;
import ru.akarpov.models.Event;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.User;
import ru.akarpov.models.forms.EventForm;
import ru.akarpov.models.forms.TicketForm;
import ru.akarpov.network.Request;
import ru.akarpov.network.Response;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientApp {
    private final Client client;
    private final BufferedReader reader;
    private boolean scriptMode = false;
    private BufferedReader scriptReader;
    private User currentUser = null;

    public ClientApp(Client client) {
        this.client = client;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }


    public void run() {
        try {
            while (currentUser == null) {
                System.out.println("Добро пожаловать! Пожалуйста, войдите или зарегистрируйтесь.");
                performAuth();
            }

            while (true) {
                System.out.print("Введите команду: ");
                String commandLine = reader.readLine();
                if (commandLine == null || "exit".equalsIgnoreCase(commandLine.trim())) {
                    System.out.println("Завершение работы клиента...");
                    break;
                }
                try {
                    handleCommand(commandLine.trim());
                } catch (Exception e) {
                    System.err.println("Ошибка при выполнении команды: " + e.getMessage());
                    tryReconnect();
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при работе клиента: " + e.getMessage());
        } finally {
            try {
                reader.close();
                client.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии соединений: " + e.getMessage());
            }
        }
    }

    private void performAuth() throws IOException {
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.print("Выберите опцию (1 или 2): ");
        String choice = reader.readLine();
        if ("1".equals(choice)) {
            performLogin();
        } else if ("2".equals(choice)) {
            performRegistration();
        } else {
            System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
        }
    }

    private void performLogin() throws IOException {
        String username = promptUsername();
        String password = promptPassword();

        String[] credentials = {username, password};
        Request request = new Request("login", credentials, null, null);

        try {
            Response response = sendRequestAndHandleResponse(request);
            if (response.isSuccess() && response.getUser() != null) {
                currentUser = response.getUser();
                System.out.println("Успешный вход. Добро пожаловать, " + currentUser.getUsername() + "!");
            } else {
                // Повторная попытка входа
                performAuth();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка при обработке ответа сервера: " + e.getMessage());
        }
    }

    private void performRegistration() throws IOException {
        String username = promptUsername();
        String password = promptPassword();

        String[] credentials = {username, password};
        Request request = new Request("register", credentials, null, null);

        try {
            Response response = sendRequestAndHandleResponse(request);
            if (response.isSuccess() && response.getUser() != null) {
                currentUser = response.getUser();
                System.out.println("Успешная регистрация. Добро пожаловать, " + currentUser.getUsername() + "!");
            } else {
                // Повторная попытка регистрации
                performAuth();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка при обработке ответа сервера: " + e.getMessage());
        }
    }

    private String promptUsername() throws IOException {
        System.out.print("Имя пользователя: ");
        return reader.readLine().trim();
    }

    private String promptPassword() throws IOException {
        String password;
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("Password: ");
            password = new String(passwordChars);
        } else {
            JPasswordField passwordField = new JPasswordField();
            Object[] message = {"Пароль:", passwordField};
            int option = JOptionPane.showConfirmDialog(null, message, "Введите пароль", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                password = new String(passwordField.getPassword());
            } else {
                throw new IOException("Ввод пароля отменен");
            }
        }
        return password.trim();
    }


    public void handleCommand(String commandLine) throws IOException, ClassNotFoundException {
        String[] parts = commandLine.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : null;

        switch (command) {
            case "execute_script":
                if (arguments != null) {
                    new ExecuteScriptCommand(this).execute(arguments);
                } else {
                    System.out.println("Не указан путь к файлу!");
                }
                break;
            case "filter_by_event":
                handleFilterByEvent();
                break;
            case "login":
                System.out.println("Вы уже вошли в систему.");
                break;
            case "register":
                handleAuthCommand(command, arguments);
                break;
            case "logout":
                handleLogout();
                break;
            default:
                sendCommandToServer(command, arguments);
                break;
        }
    }

    private void handleAuthCommand(String command, String arguments) throws IOException, ClassNotFoundException {
        if (arguments == null) {
            System.out.println("Использование: " + command + " <username> <password>");
            return;
        }
        String[] args = arguments.split("\\s+");
        if (args.length != 2) {
            System.out.println("Использование: " + command + " <username> <password>");
            return;
        }
        Request request = new Request(command, args, null, null);
        Response response = sendRequestAndHandleResponse(request);
        if (response.getUser() != null) {
            currentUser = response.getUser();
            System.out.println("Успешная " + (command.equals("login") ? "аутентификация" : "регистрация"));
        } else {
            System.out.println("Ошибка: " + response.getMessage());
        }
    }

    private void handleLogout() throws IOException, ClassNotFoundException {
        if (currentUser == null) {
            System.out.println("Вы не авторизованы.");
            return;
        }
        Request request = new Request("logout", null, null, currentUser);
        Response response = sendRequestAndHandleResponse(request);
        if (response.isSuccess()) {
            System.out.println("Вы успешно вышли из системы.");
            currentUser = null;

            // Повторное предложение залогиниться или зарегистрироваться
            performAuth();
            run();
        } else {
            System.out.println("Ошибка при выходе: " + response.getMessage());
        }
    }


    private void handleFilterByEvent() throws IOException, ClassNotFoundException {
        System.out.println("Введите данные для события:");
        EventForm eventForm = new EventForm();
        Event event = eventForm.build();
        Ticket dummyTicket = new Ticket();
        dummyTicket.setEvent(event);
        Request request = new Request("filter_by_event", null, dummyTicket, currentUser);
        Response response = sendRequestAndHandleResponse(request);

        if (response.getFilteredTickets() != null && !response.getFilteredTickets().isEmpty()) {
            System.out.println("Найденные элементы:");
            for (Ticket ticket : response.getFilteredTickets()) {
                System.out.println(ticket);
            }
        } else {
            System.out.println(response.getMessage());
        }
    }

    private void sendCommandToServer(String cmd, String argument) throws IOException, ClassNotFoundException {
        if (currentUser == null && !cmd.equals("login") && !cmd.equals("register")) {
            System.out.println("Вы должны войти в систему для выполнения этой команды.");
            return;
        }

        Request request;
        if ("add".equalsIgnoreCase(cmd) || "add_if_min".equalsIgnoreCase(cmd)) {
            TicketForm form = new TicketForm();
            form.setScriptMode(scriptMode);
            form.setUserScanner(scriptMode ? scriptReader : new BufferedReader(new InputStreamReader(System.in)));
            Ticket ticket = form.build();
            request = new Request(cmd, new String[]{argument}, ticket, currentUser);
        } else if ("update".equalsIgnoreCase(cmd)) {
            handleUpdateCommand(argument);
            return;
        } else if (argument == null) {
            request = new Request(cmd, null, null, currentUser);
        } else {
            request = new Request(cmd, new String[]{argument}, null, currentUser);
        }
        sendRequestAndHandleResponse(request);
    }

    private Response sendRequestAndHandleResponse(Request request) throws IOException, ClassNotFoundException {
        int attempts = 0;

        while (true) {
            try {
                client.sendRequest(request);
                Response response = client.receiveResponse();
                System.out.println("Ответ сервера: " + response.getMessage());
                return response;
            } catch (IOException e) {
                attempts++;
                System.err.println("Ошибка при отправке запроса (попытка " + attempts + "): " + e.getMessage());
                System.out.println("Попытка переподключения...");
                tryReconnect();
            }
        }
    }

    public void tryReconnect() {
        int reconnectAttempts = 0;
        while (true) {
            try {
                reconnectAttempts++;
                System.out.println("Попытка переподключения " + reconnectAttempts + "...");
                client.reconnect();
                System.out.println("Успешно переподключились к серверу.");
                return;
            } catch (IOException e) {
                System.err.println("Не удалось переподключиться: " + e.getMessage());
                System.out.println("Повторная попытка через 5 секунд...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.err.println("Прервано ожидание повторного подключения.");
                }
            }
        }
    }

    public void setScriptMode(boolean mode, BufferedReader reader) {
        this.scriptMode = mode;
        this.scriptReader = reader;
    }

    private void handleUpdateCommand(String argument) {
        if (argument == null || argument.isEmpty()) {
            System.out.println("Команда 'update' требует ID билета.");
            return;
        }

        long ticketId;
        try {
            ticketId = Long.parseLong(argument);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID билета должен быть числом.");
            return;
        }

        if (ticketId <= 0) {
            System.out.println("Ошибка: ID билета должен быть положительным числом.");
            return;
        }

        try {
            Request checkRequest = new Request("update", new String[]{argument}, null, currentUser);
            Response checkResponse = sendRequestAndHandleResponse(checkRequest);

            if (checkResponse.isSuccess()) {
                System.out.println(checkResponse.getMessage());
                if (!scriptMode) {
                    System.out.println("Введите новые данные для билета:");
                }

                TicketForm form = new TicketForm();
                form.setScriptMode(scriptMode);
                form.setUserScanner(scriptMode ? scriptReader : new BufferedReader(new InputStreamReader(System.in)));
                Ticket updateTicket = form.build();
                updateTicket.setId(ticketId);
                updateTicket.setUserId(currentUser.getId());

                Request updateRequest = new Request("update", new String[]{argument}, updateTicket, currentUser);
                sendRequestAndHandleResponse(updateRequest);
            } else {
                // Билет не найден или у пользователя нет прав на обновление
                System.out.println("Обновление не выполнено: " + checkResponse.getMessage());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при выполнении команды update: " + e.getMessage());
        }
    }


}