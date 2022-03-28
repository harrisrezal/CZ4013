package client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
/* 
 * Prepare to send and Recieve Message
 * 
 *  Need ServerIP Address 
 * */
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import communication.Marshal;
import communication.UnMarshal;
import response.ResponseMessage;

import utils.Constants;



public class Client  {

	public static DatagramSocket socket;
	public static SocketAddress serverSocketAddr;
	public static ByteBuffer messageByte =  ByteBuffer.allocate(8192) ;
	
	public Client()
	{
		
	}
	public void openSocketConnection(DatagramSocket socket,String serverIP, int serverPort) throws SocketException
	{
		this.socket = socket;	
        this.socket.setSoTimeout(Constants.TIMEOUT_MILLISECONDS);
        this.serverSocketAddr = new InetSocketAddress(serverIP, serverPort);
	}
	
	public void setSocketTimeOut(int milliSeconds)  throws SocketException
	{
        this.socket.setSoTimeout(milliSeconds);
	}

	
	
	
public ResponseMessage sendRequest(String method, Object obj)
{
	
       UUID id = UUID.randomUUID();
       ResponseMessage resp = null;
       boolean receive = false;
	   for(int triesLeft = Constants.MAX_ATTEMPT; triesLeft > 0; --triesLeft)
       {
		
	    try {
			this.writeToServer(id,method,obj);
						
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			receive = false;
		}
	    
	    try {
			resp = receieveFromServer();
			receive = true;
	    }catch(Exception e) {
			System.out.println("[Client] Did not receive from Server after timeout..");
			System.out.println("[Client] Re-sending requests");
			if(triesLeft == 1)
			{
				
				System.out.println("[Client] No Connection after multiple attempts" );

			}
			else 
			{
				System.out.println("[Client] Re-sending requests");

			}
			receive = false;
	      }
	    
	    if(receive)
	    {
			System.out.println("[Client] true");
	    	break;
	    }
	    
       }
	   
	   return resp;
       
	
	
}
	
	
	
	public void writeToServer(UUID id, String method, Object obj) throws IllegalAccessException 
	{
		System.out.println("[Client] The request ID is : " + id.toString() );
		System.out.println("[Client] The request Method is : " + method );
        Marshal.marshal(obj, messageByte, id, method);
        byte[] rawBuf = messageByte.array();
	    
  
            try {
    			this.socket.send(new DatagramPacket(rawBuf, rawBuf.length, serverSocketAddr));
    		} catch (IOException e) {
    			System.out.println("[Client] Did not manage to Write to Server");
    			e.printStackTrace();
    		}
    	    messageByte.clear();//Clear Buffer after Done.
	    
	}
	
	public ResponseMessage receieveFromServer()
	{
		
        byte[] rawBuf = messageByte.array();
        DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length);
        try {
            this.socket.receive(packet);
            System.out.println("[CLIENT] Received from Server");
	        System.out.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));
            ResponseMessage resp = UnMarshal.unMarshalResponse(messageByte);
    	    messageByte.clear();//Clear Buffer after Done.
            return resp;
        } catch (Exception var5) {
//            buf.close();
            throw new RuntimeException(var5);
        }

	}
	
	   public void poll(Duration interval) {
		   byte[] rawBuf = messageByte.array();
	        DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length);
	        Instant end = Instant.now().plus(interval);
	        try {
				this.setSocketTimeOut(0);
			} catch (SocketException e1) {
				System.out.println("[Client] Error Setting the socketTime out.");
				e1.printStackTrace();
			}
	        Thread pollingThread = new Thread(() -> {
	            while(!Instant.now().isAfter(end)) {
	                try {
	                    this.socket.receive(packet);

	                    try {
	                        ResponseMessage resp = UnMarshal.unMarshalResponse(messageByte);
	                    } catch (Throwable var8) {
	                        if (messageByte != null) {
	                            try {
	                            	messageByte.clear();
	                            } catch (Throwable var7) {
	                                var8.addSuppressed(var7);
	                            }
	                        }

	                        throw var8;
	                    }

	                    if (messageByte != null) {
                        	messageByte.clear();

	                    }
	                } catch (RuntimeException var9) {
	                    if (!(var9.getCause() instanceof SocketTimeoutException) && var9.getCause() instanceof InterruptedIOException) {
	                        return;
	                    }
	                } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }

	        });
	        pollingThread.run();

	        try {
	            pollingThread.join(interval.toMillis());
	        } catch (InterruptedException var7) {
	        }

	        pollingThread.interrupt();
	        try {
				this.setSocketTimeOut(Constants.TIMEOUT_MILLISECONDS);
			} catch (SocketException e) {
				System.out.println("[Client] Error Setting back the socketTime out.");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
}
