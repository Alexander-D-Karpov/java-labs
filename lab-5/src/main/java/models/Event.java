package models;

import lombok.*;

import java.util.Objects;

/**
 * Класс События.
 */
@AllArgsConstructor
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Event {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private long ticketsCount; //Значение поля должно быть больше 0
    private String description; //Поле может быть null
    private EventType eventType; //Поле может быть null

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event event = (Event) obj;
        return name.equals(event.name) &&
                ticketsCount == event.ticketsCount &&
                description.equals(event.description) &&
                eventType == event.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, ticketsCount, description, eventType);
    }
}
