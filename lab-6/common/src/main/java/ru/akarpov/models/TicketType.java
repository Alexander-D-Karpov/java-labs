package ru.akarpov.models;

import lombok.Getter;

import java.io.Serializable;

/**
 * Перечисление типов билетов.
 */
@Getter
public enum TicketType implements Serializable {
    VIP(4),
    USUAL(3),
    BUDGETARY(2),
    CHEAP(1);

    private final int status;

    TicketType(int status) {
        this.status = status;
    }
}