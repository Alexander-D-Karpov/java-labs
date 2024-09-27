package ru.akarpov.server.command;

import ru.akarpov.models.User;
import ru.akarpov.network.Response;
import ru.akarpov.server.manager.DatabaseManager;

public class LoginCommand implements CommandWithArgsInterface {
    @Override
    public Response execute(String[] args, User user) {
        if (args.length != 2) {
            return new Response("Использование: login <username> <password>", "", null, null, false);
        }
        String username = args[0];
        String password = args[1];
        User authenticatedUser = DatabaseManager.authenticateUser(username, password);
        if (authenticatedUser != null) {
            return new Response("Успешная аутентификация.", "", null, authenticatedUser, true);
        } else {
            return new Response("Ошибка аутентификации. Проверьте имя пользователя и пароль.", "", null, null, false);
        }
    }

    public String toString() {
        return " <username> <password> : войти в систему";
    }
}