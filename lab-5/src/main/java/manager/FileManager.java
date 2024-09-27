package manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import models.Ticket;

import java.io.*;
import java.util.List;
import java.util.Scanner;

/**
 * Менеджер файлов.
 */
public class FileManager {
    private final String inputFileName;
    private final ConsoleManager console;
    private final CollectionManager collectionManager;

    public FileManager(String inputFileName, ConsoleManager console, CollectionManager collectionManager) {
        this.inputFileName = inputFileName;
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
        * Проверить, можно ли читать файл.
        *
        * @param file    файл
        * @param console менеджер консоли
        * @return true, если файл нельзя читать, иначе false
    **/
    public static boolean cantRead(File file, ConsoleManager console) {
        if (!file.exists()) {
            console.printError("Файл не найден");
            return true;
        }

        if (!file.canRead()) {
            console.printError("Нет прав на чтение файла");
            return true;
        }

        if (!file.isFile()) {
            console.printError("Указанный путь не является файлом");
            return true;
        }
        return false;
    }

    /**
     * Сохранить коллекцию в файл.
     */
    public void saveCollection() {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            String xmlResult = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionManager.getCollection());
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(inputFileName))) {
                writer.write(xmlResult);
            }
        } catch (Exception e) {
            console.printError(e.getMessage());
            console.printError("Ошибка сохранения коллекции в файл");
        }
    }

    /**
     * Создать новый файл.
     *
     * @throws IOException исключение ввода-вывода
     */
    private void createNewFile() throws IOException {
        File file = new File(inputFileName);
        boolean isFileCreated = file.createNewFile();
        if (!isFileCreated) {
            console.printError("Файл не был создан");
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ArrayDeque></ArrayDeque>");
        }
    }

    /**
     * Заполнить коллекцию из файла.
     */
    public void fillCollection() {
        File file = new File(inputFileName);

        if (!file.exists()) {
            try {
                console.println("Файл " + inputFileName + " не найден. Создаю новый файл...");
                createNewFile();
                return;
            } catch (IOException e) {
                console.printError("Ошибка при создании файла: " + e.getMessage());
                return;
            }
        }

        if (cantRead(file, console)) {
            return;
        }

        try {
            XmlMapper xmlMapper = new XmlMapper();
            String xml = readFileFromScanner(file);
            List<Ticket> toCheck = xmlMapper.readValue(xml, new TypeReference<>() {
            });

            for (var ticket : toCheck) {
                if (ValidationManager.isValidTicket(ticket, collectionManager)) {
                    collectionManager.add(ticket);
                } else {
                    console.printError("Билет с id " + ticket.getId() + " не прошел валидацию");
                }
            }
        } catch (Exception e) {
            console.printError(e.getMessage());
            console.printError("Ошибка чтения файла");
        }
    }

    /**
     * Прочитать файл с помощью Scanner.
     *
     * @param file файл
     * @return содержимое файла
     */
    public String readFileFromScanner(File file) {
        StringBuilder result = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                result.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            console.printError("Файл не найден");
        }
        return result.toString();
    }
}
