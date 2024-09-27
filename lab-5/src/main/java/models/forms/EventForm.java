package models.forms;

import manager.ConsoleManager;
import manager.IdManager;
import models.Event;
import models.EventType;

import java.util.Objects;

/**
 * Форма для создания объекта класса {@link Event}.
 */
public class EventForm extends Form<Event> {
    public EventForm(ConsoleManager console) {
        super(console);
    }

    /**
     * Формирует объект класса {@link Event}.
     *
     * @return Объект класса {@link Event}
     */
    @Override
    public Event build() {
        return new Event(
                (int) IdManager.generateId(),
                askString("название события", " (строка, поле не может быть пустым)", s -> !s.isEmpty()),
                askLong("количество билетов", " (целое число, значение должно быть больше нуля)", x -> (x > 0)),
                askString("описание события", " (строка, поле не может быть пустым)", s -> !s.isEmpty()),
                askEventType()
        );
    }

    /**
     * Запрашивает у пользователя тип события.
     *
     * @return Тип события
     */
    private EventType askEventType() {
        return (EventType) askEnum("тип события", EventType.values(), Objects::nonNull);
    }
}
