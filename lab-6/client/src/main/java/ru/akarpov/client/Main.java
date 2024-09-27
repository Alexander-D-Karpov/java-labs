package ru.akarpov.client;

import ru.akarpov.client.util.Client;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        System.out.println("Подключение к серверу...");
        Client client = null;
        while (client == null) {
            try {
                client = new Client("localhost", 12727);
            } catch (IOException e) {
                System.out.println("Ошибка подключения к серверу: " + e.getMessage());
                System.out.println("Повторная попытка через 5 секунд...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    System.err.println("Ошибка при ожидании повторного подключения: " + ex.getMessage());
                }
            }
        }
        System.out.println("Подключение к серверу установлено");

        ClientApp clientApp = new ClientApp(client);
        clientApp.run();
    }
}