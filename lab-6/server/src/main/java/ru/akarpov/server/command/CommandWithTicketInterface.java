package ru.akarpov.server.command;

import ru.akarpov.models.Ticket;
import ru.akarpov.network.Response;

public interface CommandWithTicketInterface extends CommandInterface {
    Response execute(Ticket ticket);
}