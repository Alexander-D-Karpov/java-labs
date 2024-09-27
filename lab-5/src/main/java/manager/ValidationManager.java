package manager;

import models.Coordinates;
import models.Event;
import models.Ticket;

/**
 * Класс для валидации объектов.
 */
public class ValidationManager {

    private ValidationManager() {
    }

    /**
     * Проверяет валидность билета.
     *
     * @param o объект билета
     * @param collectionManager менеджер коллекции
     * @return true, если объект валиден, иначе false
     */
    public static boolean isValidTicket(Ticket o, CollectionManager collectionManager) {
        return o.getId() > 0 &&
                collectionManager.getById(o.getId()) == null &&

                o.getName() != null &&
                !o.getName().isEmpty() &&

                o.getCoordinates() != null &&
                isValidCoordinates(o.getCoordinates()) &&

                o.getCreationDate() != null &&

                o.getPrice() != null &&
                o.getPrice() > 0 &&

                o.getDiscount() != null &&
                o.getDiscount() > 0 &&
                o.getDiscount() <= 100 &&

                o.getType() != null &&

                o.getEvent() != null &&
                isValidEvent(o.getEvent());
    }

    /**
     * Проверяет валидность события.
     *
     * @param event объект события
     * @return true, если объект валиден, иначе false
     */
    private static boolean isValidEvent(Event event) {
        return event.getId() > 0 &&
                event.getName() != null &&
                !event.getName().isEmpty() &&
                event.getTicketsCount() > 0 &&
                event.getDescription() != null &&
                event.getEventType() != null;
    }

    /**
     * Проверяет валидность координат.
     *
     * @param o объект координат
     * @return true, если объект валиден, иначе false
     */
    public static boolean isValidCoordinates(Coordinates o) {
        return o.getX() <= 794;
    }
}
