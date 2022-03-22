package request;

import java.util.UUID;

public class RequestMessage {

	public UUID id;
	public String method;
	public Object requestObj;
	
	
	public RequestMessage(UUID id, String method, Object requestObj)
	{
		this.id = id;
		this.method = method;
		this.requestObj = requestObj;
	}
}
