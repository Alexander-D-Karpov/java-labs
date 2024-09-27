package ru.akarpov.server.command;

import ru.akarpov.models.User;
import ru.akarpov.network.Response;

public class LogoutCommand implements CommandInterface {
    @Override
    public Response execute(User user) {
        if (user != null) {
            return new Response("Вы успешно вышли из системы.", "", true);
        } else {
            return new Response("Вы не были аутентифицированы.", "", false);
        }
    }

    public String toString() {
        return " выход из системы";
    }
}
