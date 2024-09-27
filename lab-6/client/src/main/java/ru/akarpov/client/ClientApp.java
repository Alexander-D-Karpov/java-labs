package ru.akarpov.client;

import ru.akarpov.client.util.Client;
import ru.akarpov.client.util.ExecuteScriptCommand;
import ru.akarpov.models.Event;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.forms.EventForm;
import ru.akarpov.models.forms.TicketForm;
import ru.akarpov.network.Request;
import ru.akarpov.network.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientApp {
    private final Client client;
    private final BufferedReader reader;
    private boolean scriptMode = false;
    private BufferedReader scriptReader;

    public ClientApp(Client client) {
        this.client = client;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() {
        try {
            while (true) {
                System.out.print("Введите команду: ");
                String commandLine = scriptMode ? (scriptReader != null ? scriptReader.readLine() : null) : reader.readLine();
                if (commandLine == null || "exit".equalsIgnoreCase(commandLine.trim())) {
                    System.out.println("Завершение работы клиента...");
                    break;
                }
                try {
                    handleCommand(commandLine.trim());
                } catch (Exception e) {
                    System.err.println("Ошибка при выполнении команды: " + e.getMessage());
                    if (!scriptMode) {
                        tryReconnect();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении команды: " + e.getMessage());
        } finally {
            try {
                reader.close();
                if (scriptReader != null) {
                    scriptReader.close();
                }
                client.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии соединений: " + e.getMessage());
            }
        }
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
            default:
                sendCommandToServer(command, arguments);
                break;
        }
    }

    private void handleFilterByEvent() throws IOException, ClassNotFoundException {
        System.out.println("Введите данные для события:");
        EventForm eventForm = new EventForm();
        Event event = eventForm.build();
        Ticket dummyTicket = new Ticket();
        dummyTicket.setEvent(event);
        Request request = new Request("filter_by_event", null, dummyTicket);
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
        Request request;
        if ("add".equalsIgnoreCase(cmd) || "add_if_min".equalsIgnoreCase(cmd)) {
            TicketForm form = new TicketForm();
            form.setScriptMode(scriptMode);
            form.setUserScanner(scriptMode ? scriptReader : new BufferedReader(new InputStreamReader(System.in)));
            Ticket ticket = form.build();
            request = new Request(cmd, new String[]{argument}, ticket);
        } else if ("update".equalsIgnoreCase(cmd)) {
            handleUpdateCommand(argument);
            return;
        } else if (argument == null) {
            request = new Request(cmd, null, null);
        } else {
            request = new Request(cmd, new String[]{argument}, null);
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

        Request checkRequest = new Request("update", new String[]{argument}, null);
        try {
            Response checkResponse = sendRequestAndHandleResponse(checkRequest);

            if (checkResponse.getMessage().startsWith("Элемент с ID " + ticketId + " найден")) {
                System.out.println(checkResponse.getMessage());
                if (!scriptMode) {
                    System.out.println("Введите новые данные для билета:");
                }

                TicketForm form = new TicketForm();
                form.setScriptMode(scriptMode);
                form.setUserScanner(scriptMode ? scriptReader : new BufferedReader(new InputStreamReader(System.in)));
                Ticket updateTicket = form.build();
                updateTicket.setId(ticketId);

                Request updateRequest = new Request("update", new String[]{argument}, updateTicket);
                sendRequestAndHandleResponse(updateRequest);
            } else {
                System.out.println("Обновление не выполнено: " + checkResponse.getMessage());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при выполнении команды update: " + e.getMessage());
        }
    }
}