package server;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import client.Client;
import client.ClientServices;
import request.RequestMessage;
import utils.Constants;

public class Main  {

	
	public static void main(String[] args) throws SocketException, IllegalAccessException {


	ServerServices serverService = new ServerServices(new Server());
    DatagramSocket socket = new DatagramSocket(new InetSocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT));
    serverService.server.openSocketConnection(socket);
    System.out.printf("[Server] listening on port ://%s:%d\n", Constants.SERVER_IP,  Constants.SERVER_PORT);

    while(true) {
    	RequestMessage reqReceived = serverService.server.receieveFromClient();
    	System.out.println("[Server] Main Method is : " + reqReceived.method);
    	switch(reqReceived.method) {
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
    		serverService.processDeposit(reqReceived);
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
    	}
    }
	

}
