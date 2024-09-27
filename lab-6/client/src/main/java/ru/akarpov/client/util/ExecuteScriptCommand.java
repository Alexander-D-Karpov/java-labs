package ru.akarpov.client.util;

import ru.akarpov.client.ClientApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ExecuteScriptCommand {
    private final ClientApp clientApp;
    private static final Set<String> executingScripts = new HashSet<>();

    public ExecuteScriptCommand(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    public void execute(String filePath) {
        File file = new File(filePath);
        String absolutePath = file.getAbsolutePath();

        if (!file.exists()) {
            System.out.println("Файл не найден: " + filePath);
            return;
        }

        if (executingScripts.contains(absolutePath)) {
            System.out.println("Обнаружена рекурсия скрипта. Выполнение прервано: " + filePath);
            return;
        }

        executingScripts.add(absolutePath);

        try (BufferedReader scriptReader = new BufferedReader(new FileReader(file))) {
            clientApp.setScriptMode(true, scriptReader);
            String line;
            while ((line = scriptReader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                    System.out.println("Выполнение команды из скрипта: " + line);
                    boolean commandExecuted = false;
                    int attempts = 0;

                    while (!commandExecuted) {
                        try {
                            clientApp.handleCommand(line);
                            commandExecuted = true;
                        } catch (IOException e) {
                            attempts++;
                            System.err.println("Ошибка при выполнении команды (попытка " + attempts + "): " + e.getMessage());
                            System.out.println("Попытка переподключения...");
                            clientApp.tryReconnect();
                        } catch (ClassNotFoundException e) {
                            System.err.println("Критическая ошибка при выполнении команды: " + e.getMessage());
                            return;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Ошибка в скрипте: " + e.getMessage());
        } finally {
            clientApp.setScriptMode(false, null);
            executingScripts.remove(absolutePath);
        }
    }
}