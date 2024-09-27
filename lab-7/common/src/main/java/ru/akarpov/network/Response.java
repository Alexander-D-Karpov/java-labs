package ru.akarpov.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import ru.akarpov.models.Ticket;
import ru.akarpov.models.User;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class Response implements Serializable {
    private String message;
    private String collectionToStr;
    private List<Ticket> filteredTickets;
    private User user;
    private boolean success;

    public Response(String message, String collectionToStr) {
        this(message, collectionToStr, null, null, false);
    }

    public Response(String message, String collectionToStr, boolean success) {
        this(message, collectionToStr, null, null, success);
    }
}