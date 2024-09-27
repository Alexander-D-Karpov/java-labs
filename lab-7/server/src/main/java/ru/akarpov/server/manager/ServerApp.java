package ru.akarpov.server.manager;

import ru.akarpov.models.User;
import ru.akarpov.network.Request;
import ru.akarpov.network.Response;
import ru.akarpov.server.util.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private final InetSocketAddress address;
    private final CommandManager commandManager;
    private final CollectionManager collectionManager;
    private final ExecutorService executorService;
    private final String dbUrl;

    public ServerApp(InetSocketAddress address, CollectionManager collectionManager, CommandManager commandManager, String dbUrl) {
        this.address = address;
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.dbUrl = dbUrl;
    }

    public void run() {
        try {
            DatabaseManager.initializeDatabase(dbUrl);
            collectionManager.loadCollection();
            startServer();
        } catch (SQLException e) {
            Logger.error("Ошибка при инициализации базы данных или загрузке коллекции: " + e.getMessage(), e);
        } catch (Exception e) {
            Logger.error("Ошибка запуска сервера: " + e.getMessage(), e);
        } finally {
            executorService.shutdown();
        }
    }

    private void startServer() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(address);

        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        Logger.info("Сервер запущен на порту " + address.getPort());

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (!key.isValid()) continue;

                if (key.isAcceptable()) {
                    handleAccept(key);
                } else if (key.isReadable()) {
                    handleRead(key);
                }
            }
        }
    }

    public static class ClientHandler {
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        ByteBuffer dataBuffer;
        boolean readingSize = true;
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(key.selector(), SelectionKey.OP_READ);
        clientKey.attach(new ClientHandler());
        Logger.info("Клиент подключился: " + client.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ClientHandler handler = (ClientHandler) key.attachment();

        try {
            if (handler.readingSize) {
                int bytesRead = client.read(handler.sizeBuffer);
                if (bytesRead == -1) {
                    client.close();
                    key.cancel();
                    return;
                }
                if (!handler.sizeBuffer.hasRemaining()) {
                    handler.sizeBuffer.flip();
                    int size = handler.sizeBuffer.getInt();
                    handler.dataBuffer = ByteBuffer.allocate(size);
                    handler.readingSize = false;
                }
            }

            if (!handler.readingSize) {
                int bytesRead = client.read(handler.dataBuffer);
                if (bytesRead == -1) {
                    client.close();
                    key.cancel();
                    return;
                }
                if (!handler.dataBuffer.hasRemaining()) {
                    handler.dataBuffer.flip();
                    ByteBuffer data = handler.dataBuffer;

                    // Передаем обработку запроса в пул потоков
                    executorService.submit(() -> processRequest(client, data, key));

                    // Готовимся к чтению следующего запроса
                    handler.sizeBuffer.clear();
                    handler.dataBuffer = null;
                    handler.readingSize = true;
                }
            }
        } catch (IOException e) {
            Logger.error("Ошибка при чтении из канала", e);
            try {
                client.close();
            } catch (IOException ioException) {
                Logger.error("Ошибка при закрытии соединения с клиентом", ioException);
            }
            key.cancel();
        }
    }

    private void processRequest(SocketChannel client, ByteBuffer data, SelectionKey key) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data.array());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Request request = (Request) ois.readObject();

            Logger.info("Получен запрос: " + request.getCommandName());

            User user = request.getUser();
            Response response = commandManager.executeCommand(request.getCommandName(), request.getCommandStrArg(), request.getCommandObjArg(), user);

            sendResponse(client, response);
            Logger.info("Ответ отправлен клиенту");

        } catch (IOException | ClassNotFoundException e) {
            Logger.error("Ошибка при обработке запроса клиента", e);
            try {
                client.close();
            } catch (IOException ioException) {
                Logger.error("Ошибка при закрытии соединения с клиентом", ioException);
            }
            key.cancel();
        }
    }

    private void sendResponse(SocketChannel client, Response response) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();

        byte[] data = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();

        while (buffer.hasRemaining()) {
            client.write(buffer);
        }
    }
}