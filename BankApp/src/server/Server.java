package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

import communication.Marshal;
import communication.UnMarshal;
import request.RequestMessage;
import response.ResponseMessage;
import storage.LruCache;
import utils.Constants;

/*
 * 
 * UUID 
 * METHOD 
 * Body 
 * */
public class Server {
	
	
	/*
	 * 
	 * TODO
	 * 1) Store History of Message 
	 * 2) Filter DUplicate Message
	 * 
	 * 
	 * */
	
	public static DatagramSocket socket;
	public static SocketAddress clientSocketAddr;
	public static ByteBuffer messageByte =  ByteBuffer.allocate(8192) ;
	public static double packetLossRate = 0.1;
	public static String clientIpAddress;
	public static int clientPortNumber;
    private LruCache<UUID, ResponseMessage> cache;
    public static boolean atMostOnce;

	public Server()
	{
		
	}
	public void openSocketConnection(DatagramSocket socket, LruCache<UUID, ResponseMessage> cache, boolean atMostOnce) throws SocketException
	{
		this.socket = socket;	
		this.cache = cache;
		this.atMostOnce = atMostOnce;
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
	        System.out.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));
		    this.clientSocketAddr = new InetSocketAddress(packet.getAddress().getHostAddress(), packet.getPort());

			RequestMessage reqReceived = UnMarshal.unMarshalRequest(messageByte);

			messageByte.clear();

if(atMostOnce)
{
	ResponseMessage fromCache = this.cache.get(reqReceived.id);
	if(fromCache != null)
	{
		System.out.println("[Server] Got Duplicated Request");
		this.ReSendToClient(fromCache);
		return null;
	}
	else
	{		
		System.out.println("[Server] New  Request");
		return reqReceived;
	}
}else
{
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
	
	public void ReSendToClient(ResponseMessage resp) throws IllegalAccessException
	{
		//Need to create a new SocketAddress for each client request
		Double random = Math.random();
		System.out.println("Resend Random Value : " + String.valueOf(random));
		 if(random < this.packetLossRate)
	     {
        byte[] rawBuf = messageByte.array();
        Marshal.marshalResponse(resp, messageByte);
        DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length,this.clientSocketAddr);
        try {
			this.socket.send(packet);	
			System.out.println("[Server] ReSending Reponse to client");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     }
		   else
		     {
		    	 System.out.println("[SERVER] Drop ReResponse to Client");
		     }
		 
			messageByte.clear();

		       

	}
	
	
	

	public void sendToClient(ResponseMessage resp) throws IllegalAccessException
	{
		//Need to create a new SocketAddress for each client request
		
		Double random = Math.random();
		System.out.println("Send Random Value : " + String.valueOf(random));
   
    	 byte[] rawBuf = messageByte.array();
         Marshal.marshalResponse(resp, messageByte);
         DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length,this.clientSocketAddr);
         this.cache.put(resp.id, resp);
         if(random< this.packetLossRate)
         {
         try {
 			this.socket.send(packet);	
 			System.out.println("[Server] Send response to client");
 			messageByte.clear();
 			
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
     }
     else
     {
    	 System.out.println("[SERVER] Drop Response to Client");
     }
       
			messageByte.clear();


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
