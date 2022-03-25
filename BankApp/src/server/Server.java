package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import communication.Marshal;
import communication.UnMarshal;
import request.RequestMessage;
import response.ResponseMessage;
import utils.Constants;

/*
 * 
 * UUID 
 * METHOD 
 * Body 
 * */
public class Server {
	
	public static DatagramSocket socket;
	public static SocketAddress clientSocketAddr;
	public static ByteBuffer messageByte =  ByteBuffer.allocate(8192) ;
	
	public static String clientIpAddress;
	public static int clientPortNumber;
	
	public Server()
	{
		
	}
	public void openSocketConnection(DatagramSocket socket) throws SocketException
	{
		this.socket = socket;	
      //  this.clientSocketAddr = new InetSocketAddress(Constants.CLIENT_IP, Constants.CLIENT_PORT);
       
	}
	
	public void setSocketTimeOut(int milliSeconds)  throws SocketException
	{
        this.socket.setSoTimeout(milliSeconds);
	}

	
	public RequestMessage receieveFromClient()
	{
        byte[] rawBuf = messageByte.array();

        DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length);
        try {
			this.socket.receive(packet);
						
			System.out.println("Client Port  " + String.valueOf(packet.getPort()));
			System.out.println(packet.getAddress().getHostAddress());
		    this.clientSocketAddr = new InetSocketAddress(packet.getAddress().getHostAddress(), packet.getPort());

			RequestMessage reqReceived = UnMarshal.unMarshalRequest(messageByte);
			messageByte.clear();
			return reqReceived;
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;


	}
	

	public void sendToClient(ResponseMessage resp) throws IllegalAccessException
	{
		//Need to create a new SocketAddress for each client request

        byte[] rawBuf = messageByte.array();
        Marshal.marshalResponse(resp, messageByte);
        DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length,this.clientSocketAddr);
        try {
			this.socket.send(packet);	
			System.out.println("[Server] Send response to client");
			messageByte.clear();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public void broadcastToRegisteredClients(ResponseMessage resp, SocketAddress clientRegisteredAddress ) throws IllegalAccessException
	{
        byte[] rawBuf = messageByte.array();
        Marshal.marshalResponse(resp, messageByte);
        DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length,clientRegisteredAddress);
        try {
			this.socket.send(packet);	
			System.out.println("[Server] Send response to Registered client");
			messageByte.clear();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
}
