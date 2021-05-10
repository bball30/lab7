package serverModule.util;

import common.utility.Request;
import common.utility.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ResponseSenderThread extends Thread {
    private Response response;
    private SocketChannel socketChannel;

    public ResponseSenderThread(Response response, SocketChannel socketChannel) {
        this.response = response;
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            sendResponse(response);
        } catch (IOException exception) {
            System.out.println("Произошла ошибка при отправке ответа!");
        }
    }

    private void sendResponse(Response response) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
        byteBuffer.put(serialize(response));
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();
    }

    private byte[] serialize(Response response) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(response);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        objectOutputStream.flush();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return buffer;
    }
}
