package models;

import lombok.Getter;

/**
 * Перечисление типов билетов.
 */
@Getter
public enum TicketType {
    VIP(4),
    USUAL(3),
    BUDGETARY(2),
    CHEAP(1);

    private final int status;

    private TicketType(int status) {
        this.status = status;
    }

}