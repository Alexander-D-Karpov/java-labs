package ru.akarpov.models;

import lombok.*;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Класс Билета.
 **/

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Ticket implements Comparable<Ticket>, Serializable {
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long price; //Поле может быть null, Значение поля должно быть больше 0
    private Long discount; //Поле может быть null, Значение поля должно быть больше 0, Максимальное значение поля: 100
    private TicketType type; //Поле может быть null
    private Event event; //Поле может быть null

    @Override
    public int compareTo(Ticket other) {
        return Comparator.comparingLong(Ticket::getPrice)
                .thenComparingLong(Ticket::getId)
                .compare(this, other);
    }
}