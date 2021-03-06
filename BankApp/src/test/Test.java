package test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

class Test {
    public static void main(String[] args) throws IOException {
        String CLIENT_IP = "127.0.0.1";
        int CLIENT_PORT = 22220;
        String SERVER_IP = "192.168.186.3";
        int SERVER_PORT = 22221;

        System.out.println("Running Client");

        ByteBuffer messageByte = ByteBuffer.allocate(8192);
        messageByte.putInt(1);
        byte[] rawBuf = messageByte.array();

        DatagramSocket socket = new DatagramSocket(new InetSocketAddress(CLIENT_IP, CLIENT_PORT));
        // socket.bind(new InetSocketAddress(CLIENT_IP, CLIENT_PORT));
        InetSocketAddress serverSocketAddr = new InetSocketAddress(SERVER_IP, SERVER_PORT);
        socket.send(new DatagramPacket(rawBuf, rawBuf.length, serverSocketAddr));
    }
}