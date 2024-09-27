package ru.akarpov.server.command;

import ru.akarpov.models.User;
import ru.akarpov.network.Response;
import ru.akarpov.server.manager.DatabaseManager;

public class RegisterCommand implements CommandWithArgsInterface {
    @Override
    public Response execute(String[] args, User user) {
        if (args.length != 2) {
            return new Response("Использование: register <username> <password>", "", null, null, false);
        }
        String username = args[0];
        String password = args[1];
        User newUser = DatabaseManager.registerUser(username, password);
        if (newUser != null) {
            return new Response("Регистрация успешна.", "", null, newUser, true);
        } else {
            return new Response("Ошибка регистрации. Возможно, пользователь с таким именем уже существует.", "", null, null, false);
        }
    }

    public String toString() {
        return " <username> <password> : зарегистрировать нового пользователя";
    }
}