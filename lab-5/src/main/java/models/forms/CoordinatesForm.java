package models.forms;

import manager.ConsoleManager;
import models.Coordinates;

/**
 * Форма для создания объекта класса {@link Coordinates}.
 */
public class CoordinatesForm extends Form<Coordinates> {
    public CoordinatesForm(ConsoleManager console) {
        super(console);
    }

    /**
     * Формирует объект класса {@link Coordinates}.
     *
     * @return Объект класса {@link Coordinates}
     */
    @Override
    public Coordinates build() {
        return new Coordinates(
                askInteger("координата x", " (целое число, максимальное значение = 794)", x -> (x <= 794)),
                askInteger("координата y", " (целое число)", x -> true)
        );
    }
}
