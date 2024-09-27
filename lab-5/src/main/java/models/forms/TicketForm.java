package models.forms;

import manager.ConsoleManager;
import manager.IdManager;
import models.Coordinates;
import models.Event;
import models.Ticket;
import models.TicketType;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 * Форма для создания объекта класса {@link Ticket}.
 */
public class TicketForm extends Form<Ticket> {
    private final ConsoleManager console;

    public TicketForm(ConsoleManager console) {
        super(console);
        this.console = console;
    }

    /**
     * Формирует объект класса {@link Ticket}.
     *
     * @return Объект класса {@link Ticket}
     */
    @Override
    public Ticket build() {
        return new Ticket(
                IdManager.generateId(),
                askString("название билета", " (строка, поле не может быть пустым)", s -> !s.isEmpty()),
                askCoordinates(),
                new Date(),
                askLong("цена билета", " (целое число, не может быть пустым, значение должно быть больше нуля)", x -> (x != null && x > 0)),
                askLong("скидка", " (целое число, не может быть пустым, значение должно быть больше 0 и не больше 100)", x -> (x != null && x > 0 && x <= 100)),
                askTicketType(),
                askEvent()
        );
    }

    /**
     * Запрашивает у пользователя координаты.
     *
     * @return Координаты
     */
    private Coordinates askCoordinates() {
        return new CoordinatesForm(console).build();
    }

    /**
     * Запрашивает у пользователя тип билета.
     *
     * @return Тип билета
     */
    private TicketType askTicketType() {
        return (TicketType) askEnum("тип билета", TicketType.values(), Objects::nonNull);
    }

    /**
     * Запрашивает у пользователя событие.
     *
     * @return Событие
     */
    private Event askEvent() {
        return new EventForm(console).build();
    }
}
