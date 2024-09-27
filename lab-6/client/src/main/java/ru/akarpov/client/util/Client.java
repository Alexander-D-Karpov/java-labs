package ru.akarpov.client.util;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import ru.akarpov.network.Request;
import ru.akarpov.network.Response;

public class Client {
    private SocketChannel channel;
    private final String host;
    private final int port;
    private static final int RECONNECT_DELAY = 5000;

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.channel = connect();
    }

    private SocketChannel connect() throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(host, port));
        while (!channel.finishConnect()) {
            // Ожидаем завершения подключения
        }
        return channel;
    }

    public void sendRequest(Request request) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
        objectOut.writeObject(request);
        objectOut.flush();
        byte[] data = byteOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    public Response receiveResponse() throws IOException, ClassNotFoundException {
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        while (sizeBuffer.hasRemaining()) {
            int bytesRead = channel.read(sizeBuffer);
            if (bytesRead == -1) {
                throw new IOException("Сервер закрыл соединение");
            }
        }
        sizeBuffer.flip();
        int size = sizeBuffer.getInt();

        ByteBuffer dataBuffer = ByteBuffer.allocate(size);
        while (dataBuffer.hasRemaining()) {
            int bytesRead = channel.read(dataBuffer);
            if (bytesRead == -1) {
                throw new IOException("Сервер закрыл соединение");
            }
        }
        dataBuffer.flip();

        ByteArrayInputStream byteIn = new ByteArrayInputStream(dataBuffer.array());
        ObjectInputStream objectIn = new ObjectInputStream(byteIn);
        return (Response) objectIn.readObject();
    }

    public void close() throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
    }

    public void reconnect() throws IOException {
        int attempts = 0;

        while (true) {
            try {
                close();
                Thread.sleep(RECONNECT_DELAY);
                this.channel = connect();
                System.out.println("Переподключение к серверу выполнено успешно.");
                return;
            } catch (IOException e) {
                attempts++;
                System.err.println("Попытка переподключения " + attempts + " не удалась: " + e.getMessage());
                System.err.println("Следующая попытка через " + (RECONNECT_DELAY / 1000) + " секунд...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Прерывание во время ожидания переподключения", e);
            }
        }
    }
}