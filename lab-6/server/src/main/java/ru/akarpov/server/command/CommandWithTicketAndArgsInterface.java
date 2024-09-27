package ru.akarpov.server.command;

import ru.akarpov.models.Ticket;
import ru.akarpov.network.Response;

public interface CommandWithTicketAndArgsInterface extends CommandInterface {
    Response execute(String[] args, Ticket ticket);
}