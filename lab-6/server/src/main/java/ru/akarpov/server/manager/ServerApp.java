package ru.akarpov.server.manager;

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
import java.util.Iterator;
import java.util.Set;

public class ServerApp {
    private final InetSocketAddress address;
    private final CommandManager commandManager;
    private final FileManager fileManager;

    public ServerApp(InetSocketAddress address, CollectionManager collectionManager, CommandManager commandManager, FileManager fileManager) {
        this.address = address;
        this.commandManager = commandManager;
        this.fileManager = fileManager;
        IdManager.setCollectionManager(collectionManager);
    }

    public void run(String[] args) {
        try {
            fileManager.fillCollection();
            Logger.info("Коллекция загружена");
            startServer(); // Инициируем запуск сервера
        } catch (Exception e) {
            Logger.error("Ошибка запуска сервера", e);
        } finally {
            fileManager.saveCollection();
            Logger.info("Коллекция сохранена в файл.");
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
            selector.select(); // Blocking until something is registered
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

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel client = serverChannel.accept();
        if (client != null) {
            client.configureBlocking(false);
            client.register(key.selector(), SelectionKey.OP_READ);
            Logger.info("Клиент подключился: " + client.getRemoteAddress());
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        ByteBuffer dataBuffer = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Чтение размера данных
            while (sizeBuffer.hasRemaining()) {
                int bytesRead = client.read(sizeBuffer);
                if (bytesRead == -1) {
                    throw new EOFException("Клиент отключился: " + client.getRemoteAddress());
                }
            }
            sizeBuffer.flip();
            int size = sizeBuffer.getInt();

            // Чтение данных
            dataBuffer = ByteBuffer.allocate(size);
            while (dataBuffer.hasRemaining()) {
                int bytesRead = client.read(dataBuffer);
                if (bytesRead == -1) {
                    throw new EOFException("Клиент отключился: " + client.getRemoteAddress());
                }
            }
            dataBuffer.flip();
            baos.write(dataBuffer.array(), 0, dataBuffer.limit());

            // Процесс десериализации объекта Request
            byte[] completeData = baos.toByteArray();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(completeData));
            Request request = (Request) ois.readObject();

            // Обработка запроса и отправка ответа
            Response response = commandManager.executeCommand(request.getCommandName(), request.getCommandStrArg(), request.getCommandObjArg());
            sendResponse(client, response);

        } catch (ClassNotFoundException e) {
            Logger.error("Класс не найден", e);
        } catch (EOFException e) {
            Logger.info("Клиент отключился (EOF): " + client.getRemoteAddress());
            client.close();
            key.cancel();
        } catch (IOException e) {
            Logger.error("Ошибка при чтении из канала", e);
            client.close();
            key.cancel();
        } finally {
            // Очистка буферов и закрытие потоков
            if (sizeBuffer != null) {
                sizeBuffer.clear();
            }
            if (dataBuffer != null) {
                dataBuffer.clear();
            }
            baos.close();
        }
    }


    private void sendResponse(SocketChannel client, Response response) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
        objectOut.writeObject(response);
        objectOut.flush();
        byte[] data = byteOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        while (buffer.hasRemaining()) {
            client.write(buffer);
        }
    }
}

