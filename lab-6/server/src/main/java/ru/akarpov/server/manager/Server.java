package ru.akarpov.server.manager;

import ru.akarpov.network.Request;
import ru.akarpov.network.Response;
import ru.akarpov.server.util.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;

    public Server(ServerSocketChannel serverChannel, Selector selector, CollectionManager collectionManager, CommandManager commandManager) {
        this.serverChannel = serverChannel;
        this.selector = selector;
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    public void start() throws IOException {
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    handleAccept(serverChannel, selector);
                }
                if (key.isReadable()) {
                    handleRead(key);
                }

                iter.remove();
            }
        }
    }

    private void handleAccept(ServerSocketChannel serverChannel, Selector selector) throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        Logger.info("Accepted new connection from client: " + client.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024); // Выделяем буфер для чтения
        int numRead;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Чтение данных клиента в буфер
        while ((numRead = client.read(buffer)) > 0) {
            buffer.flip();
            byte[] data = new byte[numRead];
            buffer.get(data);
            baos.write(data);
            buffer.clear();
        }

        if (numRead == -1) {
            Logger.info("Disconnected client: " + client.getRemoteAddress());
            client.close();
            key.cancel();
            return;
        }

        try {
            // Десериализация объекта Request из полученных данных
            byte[] completeData = baos.toByteArray();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(completeData));
            Request request = (Request) ois.readObject();

            // Обработка запроса
            Response response = commandManager.executeCommand(request.getCommandName(), request.getCommandStrArg(), request.getCommandObjArg());

            // Отправка ответа
            sendResponse(client, response);
        } catch (ClassNotFoundException | IOException e) {
            Logger.error("Failed to read from channel: " + e.getMessage(), e);
        }
    }

    private void sendResponse(SocketChannel client, Response response) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
        objectOut.writeObject(response);
        objectOut.flush();
        ByteBuffer writeBuffer = ByteBuffer.wrap(byteOut.toByteArray());
        while (writeBuffer.hasRemaining()) {
            client.write(writeBuffer);
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        SocketChannel socketChannel = (SocketChannel) channel.register(selector, SelectionKey.OP_READ).channel();
        System.out.println("Client connected: " + socketChannel.getRemoteAddress());
    }

    private void read(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int numRead = channel.read(buffer);

            if (numRead == -1) {
                System.out.println("Connection closed by: " + channel.getRemoteAddress());
                channel.close();
                key.cancel();
                return;
            }

            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Request request = (Request) ois.readObject();

            handleRequest(channel, request);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to read from channel: " + e.getMessage());
        }
    }

    private void handleRequest(SocketChannel channel, Request request) throws IOException {
        Response response = commandManager.executeCommand(request.getCommandName(), request.getCommandStrArg(), request.getCommandObjArg());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(response);
        oos.flush();
        byte[] bytes = bos.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}
