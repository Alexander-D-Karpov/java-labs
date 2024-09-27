package manager;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Менеджер выполнения программы.
 */
@AllArgsConstructor
public class RuntimeManager {
    private CommandManager commandManager;
    private FileManager fileManager;
    private ConsoleManager console;

    /**
     * Интерактивный режим.
     */
    public void interactiveMode() {
        Scanner scanner = ScannerManager.getScanner();
        fileManager.fillCollection();
        console.println("Добро пожаловать в программу! Введите help для получения справки.");

        // Setup shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                fileManager.saveCollection();
                console.println("Завершение работы программы. Данные сохранены в файл.");
            } catch (Exception e) {
                console.printError("Ошибка при сохранении данных: " + e.getMessage());
            }
        }, "Shutdown-thread"));

        while (true) {
            try {
                console.print(">>> ");
                String[] userCommand = scanner.nextLine().trim().split(" ");
                commandManager.executeCommand(userCommand[0].toLowerCase(), Arrays.copyOfRange(userCommand, 1, userCommand.length));
            } catch (NoSuchElementException e) {
                return; // Exit the loop when scanner is closed
            } catch (Exception e) {
                console.printError(e.getMessage());
            }
        }
    }
}
