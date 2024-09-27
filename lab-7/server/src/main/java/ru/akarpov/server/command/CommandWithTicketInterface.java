package ru.akarpov.server.command;

import ru.akarpov.models.Ticket;
import ru.akarpov.models.User;
import ru.akarpov.network.Response;


public interface CommandWithTicketInterface {
    Response execute(Ticket ticket, User user);
}