package ru.akarpov.server.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ru.akarpov.models.Ticket;
import ru.akarpov.server.util.Logger;

import java.io.*;
import java.util.List;

/**
 * Менеджер файлов.
 */
public class FileManager {
    private final String inputFileName;
    private final CollectionManager collectionManager;

    public FileManager(String inputFileName, CollectionManager collectionManager) {
        this.inputFileName = inputFileName;
        this.collectionManager = collectionManager;
    }

    /**
     * Проверить, можно ли читать файл.
     *
     * @param file    файл
     * @return true, если можно читать файл, иначе false
     */
    public static boolean canRead(File file) {
        if (!file.exists()) {
            Logger.warn("Файл не найден: " + file.getAbsolutePath());
            return false;
        }

        if (!file.canRead()) {
            Logger.warn("Нет прав на чтение файла: " + file.getAbsolutePath());
            return false;
        }

        if (!file.isFile()) {
            Logger.warn("Указанный путь не является файлом: " + file.getAbsolutePath());
            return false;
        }

        return true;
    }

    /**
     * Сохранить коллекцию в файл.
     */
    public void saveCollection() {
        try {
            XmlMapper mapper = new XmlMapper();
            String xmlResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionManager.getCollection());
            try (FileOutputStream fos = new FileOutputStream(inputFileName)) {
                fos.write(xmlResult.getBytes());
                fos.flush();
                Logger.info("Коллекция сохранена в файл: " + inputFileName);
            }
        } catch (Exception e) {
            Logger.error("Ошибка сохранения коллекции в файл", e);
        }
    }

    /**
     * Заполнить коллекцию из файла.
     */
    public void fillCollection() {
        File file = new File(inputFileName);
        if (!canRead(file)) {
            return;
        }

        try {
            XmlMapper xmlMapper = new XmlMapper();
            String xml = bufferedReaderToString(new BufferedReader(new FileReader(file)));
            List<Ticket> tickets = xmlMapper.readValue(xml, new TypeReference<>() {
            });

            for (var ticket : tickets) {
                if (ValidationManager.isValidTicket(ticket, collectionManager)) {
                    collectionManager.add(ticket);
                } else {
                    Logger.warn("Билет с id " + ticket.getId() + " не прошел валидацию");
                }
            }
            Logger.info("Коллекция успешно загружена из файла: " + inputFileName);
        } catch (Exception e) {
            Logger.error("Ошибка чтения файла", e);
        }
    }

    /**
     * Преобразовать BufferedReader в строку.
     *
     * @param br BufferedReader
     * @return строка
     * @throws IOException ошибка ввода/вывода
     */
    public String bufferedReaderToString(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}