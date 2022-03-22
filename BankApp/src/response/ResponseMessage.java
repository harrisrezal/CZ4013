package response;

import java.util.UUID;

public class ResponseMessage {
	
	public UUID id;
	public String method;
	public Status status;
	public Object obj;
	
	public ResponseMessage()
	{
		
	}
	public ResponseMessage(UUID id,String method, Status status,Object obj) {
		this.id = id;
		this.status = status;
		this.method = method;
		this.obj = obj;
	}
	
	@Override
	public String toString() {
		return "ResponseMessage [id=" + id + ", status=" + status + ", method=" + method + ", obj=" + obj + "]";
	}

}
