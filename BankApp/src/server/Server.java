package server;

import communication.Marshal;
import communication.UnMarshal;
import request.RequestMessage;
import response.ResponseMessage;
import storage.LruCache;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

/*
 *
 * UUID
 * METHOD
 * Body
 * */
public class Server {

    // environment variables
    public static Map<String, String> env = System.getenv();

    public static DatagramSocket socket;
    public static SocketAddress clientSocketAddr;
    public static ByteBuffer messageByte = ByteBuffer.allocate(8192);
    public static double packetLossRate = Double.parseDouble(env.getOrDefault("PACKET_LOSS_RATE", "0.0"));
    public static String clientIpAddress;
    public static int clientPortNumber;
    private LruCache<UUID, ResponseMessage> cache;
    public static boolean atMostOnce;

    public Server() {

    }

    public void openSocketConnection(DatagramSocket socket, LruCache<UUID, ResponseMessage> cache, boolean atMostOnce) throws SocketException {
        Server.socket = socket;
        this.cache = cache;
        Server.atMostOnce = atMostOnce;
        //  this.clientSocketAddr = new InetSocketAddress(Constants.CLIENT_IP, Constants.CLIENT_PORT);

    }

    public void setSocketTimeOut(int milliSeconds) throws SocketException {
        socket.setSoTimeout(milliSeconds);
    }


    public RequestMessage receieveFromClient() {
        byte[] rawBuf = messageByte.array();

        DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length);
        try {
            socket.receive(packet);
            double value = Math.random();

            if (value >= packetLossRate) {
                System.out.println("Value = " + value + " || packetLossRate = " + packetLossRate);
                System.out.println("Dropping Clients request packet on PURPOSE!!");
                return null;
            }

            System.out.println("Client Port  " + packet.getPort());
            System.out.println(packet.getAddress().getHostAddress());
            System.out.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));
            clientSocketAddr = new InetSocketAddress(packet.getAddress().getHostAddress(), packet.getPort());

            RequestMessage reqReceived = UnMarshal.unMarshalRequest(messageByte);

            messageByte.clear();

            if (atMostOnce) {
                ResponseMessage fromCache = this.cache.get(reqReceived.id);
                if (fromCache != null) {
                    System.out.println("[Server] Got Duplicated Request");
                    this.ReSendToClient(fromCache);
                    return null;
                } else {
                    System.out.println("[Server] New  Request");
                    return reqReceived;
                }
            } else {
                System.out.println("[Server] At Least Once Request");
                return reqReceived;
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;


    }

    public void ReSendToClient(ResponseMessage resp) throws IllegalAccessException {
        //Need to create a new SocketAddress for each client request
        Double random = Math.random();
        System.out.println("Resend Random Value : " + random);
        if (random < packetLossRate) {
            byte[] rawBuf = messageByte.array();
            Marshal.marshalResponse(resp, messageByte);
            DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length, clientSocketAddr);
            try {
                socket.send(packet);
                System.out.println("[Server] ReSending Reponse to client");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("[SERVER] Drop ReResponse to Client");
        }

        messageByte.clear();

    }


    public void sendToClient(ResponseMessage resp) throws IllegalAccessException {
        //Need to create a new SocketAddress for each client request

        Double random = Math.random();
        System.out.println("[Server] Math Random Value is : " + random);

        byte[] rawBuf = messageByte.array();
        Marshal.marshalResponse(resp, messageByte);
        DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length, clientSocketAddr);
        this.cache.put(resp.id, resp);
        if (random < packetLossRate) {
            try {
                socket.send(packet);
                System.out.println("[Server] Successfully send response to client");
                messageByte.clear();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.out.println("[SERVER] Drop Response to Client");
        }

        messageByte.clear();


    }

    public void broadcastToRegisteredClients(ResponseMessage resp, SocketAddress clientRegisteredAddress) throws IllegalAccessException {
        byte[] rawBuf = messageByte.array();
        Marshal.marshalResponse(resp, messageByte);
        DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length, clientRegisteredAddress);
        try {
            socket.send(packet);
            System.out.println("[Server] Successfully send response to Registered client");
            messageByte.clear();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

}
