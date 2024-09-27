package ru.akarpov.server.manager;

import ru.akarpov.server.util.Logger;

/**
 * Класс для генерации id.
 */
public class IdManager {
    private static long id = 1;
    private static CollectionManager collectionManager;

    private IdManager() {
    }


    public static void setCollectionManager(CollectionManager collectionManager) {
        IdManager.collectionManager = collectionManager;
    }

    /**
     * Генерация id.
     *
     * @return id
     */
    public static long generateId() {
        if (collectionManager == null) {
            throw new NullPointerException("CollectionManager не инициализирован");
        }
        while (collectionManager.getById(id) != null){
            id++;
        }
        Logger.info("Сгенерирован новый id: " + id);
        return id;
    }

    public static int generateEventId() {
        if (collectionManager == null) {
            throw new NullPointerException("CollectionManager не инициализирован");
        }
        int eventId = 1;
        while (collectionManager.getEventById(eventId) != null) {
            eventId++;
        }
        Logger.info("Сгенерирован новый id для Event: " + eventId);
        return eventId;
    }
}