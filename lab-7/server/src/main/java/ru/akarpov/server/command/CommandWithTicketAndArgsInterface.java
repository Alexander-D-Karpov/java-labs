package ru.akarpov.server.command;

import ru.akarpov.models.Ticket;
import ru.akarpov.models.User;
import ru.akarpov.network.Response;

public interface CommandWithTicketAndArgsInterface {
    Response execute(String[] args, Ticket ticket, User user);
}