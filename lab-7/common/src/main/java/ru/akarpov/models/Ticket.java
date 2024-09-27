package ru.akarpov.models;

import lombok.*;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Ticket implements Comparable<Ticket>, Serializable {
    private long id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private Long price;
    private Long discount;
    private TicketType type;
    private Event event;
    private int userId;

    @Override
    public int compareTo(Ticket other) {
        return Comparator.comparingLong(Ticket::getPrice)
                .thenComparingLong(Ticket::getId)
                .compare(this, other);
    }
}