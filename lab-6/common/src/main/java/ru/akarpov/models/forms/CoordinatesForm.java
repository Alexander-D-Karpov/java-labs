package ru.akarpov.models.forms;

import ru.akarpov.models.Coordinates;

/**
 * Форма для создания объекта класса {@link Coordinates}.
 */
public class CoordinatesForm extends Form<Coordinates> {
    public CoordinatesForm() {
        super();
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
