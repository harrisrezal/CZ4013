package server;

import request.RequestMessage;
import response.ResponseMessage;
import storage.LruCache;
import utils.Constants;

import java.net.*;
import java.util.UUID;

public class Main {


    public static void main(String[] args) throws SocketException, IllegalAccessException, UnknownHostException {

        boolean atMostOnce = true; //True for atMostonce, false for at least once.
        ServerServices serverService = new ServerServices(new Server());
        System.out.println(InetAddress.getByName(Constants.SERVER_IP));
        DatagramSocket socket = new DatagramSocket(null);
        socket.bind(new InetSocketAddress(InetAddress.getByName(Constants.SERVER_IP), Constants.SERVER_PORT));
        ServerServices.server.openSocketConnection(socket, new LruCache<UUID, ResponseMessage>(atMostOnce ? 1024 : 0), atMostOnce);
        System.out.printf("[Server] listening on port ://%s:%d\n", Constants.SERVER_IP, Constants.SERVER_PORT);
        while (true) {
            RequestMessage reqReceived = ServerServices.server.receieveFromClient();
            if (reqReceived != null) {
                System.out.println("[Server] Main Method is : " + reqReceived.method);
                switch (reqReceived.method) {
                    case "OpenAccount":
                        serverService.processOpenAccount(reqReceived);
                        break;
                    case "QueryAccount":
                        serverService.queryAccount(reqReceived);
                        break;
                    case "DepositAccount":
                        serverService.processDeposit(reqReceived);
                        break;
                    case "MonitorAccount":
                        serverService.processMonitor(reqReceived);
                        break;
                    case "PayMaintenanceFee":
                        serverService.processPayMaintenanceFee(reqReceived);
                        break;
                    case "CloseAccount":
                        serverService.processCloseAccount(reqReceived);
                        break;
                    default:
                        System.out.println("[Server] Invalid Method to execute!");
                        break;
                }


            } else {
                System.out.println("[Server] Re-sending Duplicated Request.");
            }
        }
    }


}
