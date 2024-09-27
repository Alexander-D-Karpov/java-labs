package ru.akarpov.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

import ru.akarpov.models.Ticket;

@AllArgsConstructor
@ToString
@Getter
public class Request implements Serializable {
    private final String commandName;
    private final String[] commandStrArg;
    private final Ticket commandObjArg;

}
