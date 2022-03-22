package communication;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.lang.reflect.Type;

import request.CloseAccountRequest;
import request.DepositAccountRequest;
import request.MaintenanceFeeAccountRequest;
import request.MonitorAccountRequest;
import request.OpenAccountRequest;
import request.QueryAccountRequest;
import request.RequestMessage;
import response.CloseAccountResponse;
import response.DepositAccountResponse;
import response.MaintenanceFeeAccountResponse;
import response.MonitorAccountResponse;
import response.MonitorAccountStatusResponse;
import response.OpenAccountResponse;
import response.QueryAccountResponse;
import response.ResponseMessage;
import response.Status;

public class UnMarshal {

	
	public UnMarshal()
	{
		
	}
	
	//For Client -> Server Request.[Done on Server Side]
	public static RequestMessage unMarshalRequest(ByteBuffer buf) {
	     
		UUID id = retrieveUUID(buf);
        String method = retrieveMethod(buf);
        
        System.out.println("[SERVER] Umarshall the ID : " + id.toString());
        System.out.println("[SERVER] Umarshall the Method : " + method);

        Object obj = getClassOfRequest(method);
        Class methodClass = obj.getClass(); 
		Field[] fieldList = methodClass.getDeclaredFields();

		 for (Field field : fieldList)
		 {
			 try {
				writeToObject(field, buf,obj);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		 }
		return new RequestMessage(id,method,obj);

	     /*
	      * 1) GET UUID
	      * 2) GET METHOD
	      * 3) GET Class from the specific method
	      * 4) For each field, Unmarshall it 
	      * 5) Reset the Buffer
	      * 6) Return Object
	      * 
	      * */
	     
	     
	}
	
	//For Server -> Client [Done on Client side]
	public static ResponseMessage unMarshalResponse(ByteBuffer buf)
	{
		UUID id = retrieveUUID(buf);
		String method = retrieveMethod(buf);
		Status status =  (Status) retrieveStatus(buf);
		    Object obj = getClassOfResponse(method);
	        Class methodClass = obj.getClass(); 
			Field[] fieldList = methodClass.getDeclaredFields();
			
			for (Field field : fieldList)
			 {
				 try {
					writeToObject(field, buf,obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			 }
			
			return new ResponseMessage(id,method,status,obj);
	}
	
	public static void writeToObject(Field field, ByteBuffer buf, Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		
		Class clazz = field.getType();
				
		
		if(clazz == Byte.TYPE)
		{
			Object x = readFromBuffer(Byte.TYPE, buf);
            field.setByte(obj, (Byte)x);
		} else if (clazz == Boolean.TYPE) {

			Object x = readFromBuffer(Boolean.TYPE, buf);
            field.setBoolean(obj, (Boolean)x);
		}else if (clazz == Long.TYPE) {

			Object x = readFromBuffer(Long.TYPE, buf);
            field.setLong(obj, (Long)x);
		}
		else if (clazz == Integer.TYPE) {

			Object x = readFromBuffer(Integer.TYPE, buf);
            field.setInt(obj, (Integer)x);
		}
		else if (clazz == Double.TYPE) {

			Object x = readFromBuffer(Double.TYPE, buf);
            field.setDouble(obj, (Double)x);
		}
		else {

			Object x = readFromBuffer(clazz,clazz.getTypeName(), buf);
            field.set(obj, x);
		}
		

	}
	
	
	public static Object readFromBuffer(Type type, ByteBuffer buf )
	{

		Object value = null;
		if(type == Byte.TYPE)
		{
			value = buf.get();
		}
		else if (type == Boolean.TYPE)
		{		
			 Byte b = buf.get();
             switch(b) {
             case 0:
            	 value = false;
            	 break;
             case 1:
            	 value = true;
            	 break;
             default:
                 throw new MarshalException(String.format("Unexpected byte %d while reading a bool value at offset %d", b, buf.position()));
             }
		}
		
		else if (type == Long.TYPE)
		{
			value = buf.getLong();
		}
		
		else if(type == Integer.TYPE)
		{
			value = buf.getInt();
		}
		
		else if (type == Double.TYPE)
		{
			value = buf.getDouble();
		}
		return value;
		
	}
	
	//For string values and other objects
	public static Object readFromBuffer(Class clazz,String typeName, ByteBuffer buf )
	{
			Object value = null;
			if(typeName == "java.lang.String")
			{
				int len = buf.getInt();
		        byte[] utf8 = new byte[len];
		        buf.get(utf8);
		        value = new String(utf8, StandardCharsets.UTF_8);
			}
			
			else
			{
				 int i = buf.get();
                 Object[] cs = clazz.getEnumConstants();
                 if (i >= 0 && i <= cs.length) {
                     value =  cs[i];
                 } else {
                     throw new MarshalException(String.format("Invalid ordinal %d of %s at offset %d.", Integer.valueOf(i), clazz.getName(), buf.position()));
                 }
			}
		
		
		return value;
		
	}
	
	public static Object getClassOfRequest(String method)
	{
		System.out.println("[Method] : ");
		System.out.print(method);
		Object obj = null;
		if(method.equals("OpenAccount"))
		{	

			 obj = new OpenAccountRequest();
				System.out.println("[Method] Open : ");
			 
		}else if(method.equals("QueryAccount"))
		{
			 obj = new QueryAccountRequest();
				System.out.println("[Method] Query: ");

		}
		
		else if(method.equals("DepositAccount"))
		{
			 obj = new DepositAccountRequest();

		}
		
		else if(method.equals("MonitorAccount"))
		{
			 obj = new MonitorAccountRequest();

		}
		else if(method.equals("PayMaintenanceFee"))
		{
			 obj = new MaintenanceFeeAccountRequest();

		}
		else if(method.equals("CloseAccount"))
		{
			 obj = new CloseAccountRequest();

		}
			return obj;
		
	}
	
	public static Object getClassOfResponse(String method)
	{
		Object obj = null;
		if(method.equals("OpenAccount"))
		{
			obj = new OpenAccountResponse();
		}else if(method.equals("QueryAccount"))
		{
			 obj = new QueryAccountResponse();
		}
		else if(method.equals("DepositAccount"))
		{
			 obj = new DepositAccountResponse();
		}
		else if(method.equals("MonitorAccount"))
		{
			 obj = new MonitorAccountStatusResponse();

		}
		else if(method.equals("MonitorInfo"))
		{
			 obj = new MonitorAccountResponse();

		}	
		else if(method.equals("PayMaintenanceFee"))
		{
			 obj = new MaintenanceFeeAccountResponse();

		}		else if(method.equals("CloseAccount"))
		{
			 obj = new CloseAccountResponse();

		}
		
		
			return obj;
		
	}
	
	
	
	public static UUID retrieveUUID(ByteBuffer buf)
	{
		return new UUID(buf.getLong(), buf.getLong());
		
	}
	
	public static String retrieveMethod(ByteBuffer buf)
	{
		 	int len = buf.getInt();
	        byte[] utf8 = new byte[len];
	        buf.get(utf8);
	        return new String(utf8, StandardCharsets.UTF_8);
		
	}
	
	
	public static Object retrieveStatus(ByteBuffer buf)
	{
		Object value = null;
		 int i = buf.get();
         Object[] cs = Status.class.getEnumConstants();
         if (i >= 0 && i <= cs.length) {
             value =  cs[i];
         } else {
             throw new MarshalException(String.format("Invalid ordinal"));
         }
         
         return value;
		
	}
}
