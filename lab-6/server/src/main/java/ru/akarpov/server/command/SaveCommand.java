package ru.akarpov.server.command;

import ru.akarpov.server.manager.FileManager;
import ru.akarpov.network.Response;

public class SaveCommand implements CommandInterface {
    private final FileManager fileManager;

    public SaveCommand(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public Response execute() {
        fileManager.saveCollection();
        return new Response("Коллекция сохранена в файл.", "");
    }

    @Override
    public String toString() {
        return ": сохранить коллекцию в файл";
    }
}