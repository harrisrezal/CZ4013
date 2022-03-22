package server;

import java.util.HashMap;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import client.Client;
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
import storage.AccountDetail;
import storage.Database;
import utils.Constants;

public class ServerServices {
	
public static Server server;

	private int nextAvailableAccountNumber = 1;
    private Database db = new Database();
    private Map<SocketAddress, Instant> listeners = new HashMap();

    //TODO: if failed request, return status failed.
    public ServerServices(Server server)
    {
    	this.server =  server;
    }

    
    public void processOpenAccount(RequestMessage reqReceived) throws IllegalAccessException {
    	OpenAccountRequest req = (OpenAccountRequest) reqReceived.requestObj;
        int accountNumber = this.nextAvailableAccountNumber++;
        this.db.store(accountNumber, new AccountDetail(req.name, req.password, req.currency, req.balance));
        this.server.sendToClient(new ResponseMessage(reqReceived.id,reqReceived.method,Status.OK, new OpenAccountResponse(accountNumber)));
        this.broadcast(String.format("User %s opens new account with number %d", req.name, accountNumber));
    }

    public void queryAccount(RequestMessage reqReceived) throws IllegalAccessException {
    	
    	QueryAccountRequest req = (QueryAccountRequest) reqReceived.requestObj;
    	AccountDetail accountDetail  = this.db.query(req.accountNumber);
    	QueryAccountResponse resp = null;
         if (accountDetail == null) {
        	 resp = QueryAccountResponse.failed("This account number doesn't exist");
         } else if (!req.password.equals(accountDetail.password)) {
        	 resp = QueryAccountResponse.failed("You've entered a Wrong password");
         } else {
             resp = new QueryAccountResponse(accountDetail.name, accountDetail.currency, accountDetail.amount, true, "");
         }
         this.server.sendToClient(new ResponseMessage(reqReceived.id,reqReceived.method,Status.OK, resp));
         this.broadcast(String.format("Someone queries account %d", req.accountNumber));

    }
    
    
    public void processDeposit(RequestMessage reqReceived) throws IllegalAccessException {
    	
    
    	DepositAccountRequest req = (DepositAccountRequest) reqReceived.requestObj;
    	DepositAccountResponse resp = null;
    	

        AccountDetail accountDetail = this.db.query(req.accountNumber);
        if (accountDetail == null) {
        	resp = DepositAccountResponse.failed("This account number doesn't exist");
        } else if (!accountDetail.name.equals(req.name)) {
        	resp = DepositAccountResponse.failed("The account number is not under this name");
        } else if (!accountDetail.password.equals(req.password)) {
        	resp = DepositAccountResponse.failed("Wrong password");
        } else if (accountDetail.currency != req.currency) {
        	resp = DepositAccountResponse.failed("The currency doesn't match");
        } else if (accountDetail.amount + req.amount < 0.0D) {
        	resp = DepositAccountResponse.failed("There's not enough balance to withdraw");
        } else {
            this.db.store(req.accountNumber, new AccountDetail(accountDetail.name, accountDetail.password, accountDetail.currency, accountDetail.amount + req.amount));
            accountDetail = this.db.query(req.accountNumber);
            if (req.amount > 0.0D) {
            	resp = new DepositAccountResponse(accountDetail.name,req.amount,accountDetail.currency,accountDetail.amount,true,"Deposit","");
                this.broadcast(String.format("User %s deposit %f %s to account %d", req.name, req.amount, req.currency, req.accountNumber));

            } else {
            	resp = new DepositAccountResponse(accountDetail.name,req.amount,accountDetail.currency,accountDetail.amount,true,"Withdraw","");
                this.broadcast(String.format("User %s withdraw %f %s from account %d", req.name, -req.amount, req.currency, req.accountNumber));
            }
            this.server.sendToClient(new ResponseMessage(reqReceived.id,reqReceived.method,Status.OK, resp));
        }
    }
    
    public void processPayMaintenanceFee(RequestMessage reqReceived) throws IllegalAccessException {
    	
    	MaintenanceFeeAccountRequest req = (MaintenanceFeeAccountRequest) reqReceived.requestObj;
    	MaintenanceFeeAccountResponse resp = null;
    	
        AccountDetail accountDetail = this.db.query(req.accountNumber);
        if (accountDetail == null) {
        	resp =  MaintenanceFeeAccountResponse.failed("This account number doesn't exist");
        } else {
            this.db.store(req.accountNumber, new AccountDetail(accountDetail.name, accountDetail.password, accountDetail.currency, accountDetail.amount * 0.99D));
            accountDetail = this.db.query(req.accountNumber);
            this.broadcast(String.format("User %s pays maintenance fee for account %d", req.name, req.accountNumber));
            resp = new MaintenanceFeeAccountResponse(accountDetail.currency, accountDetail.amount, true, "");
        }
        
        this.server.sendToClient(new ResponseMessage(reqReceived.id,reqReceived.method,Status.OK, resp));

    }
    
    public void processCloseAccount(RequestMessage reqReceived) throws IllegalAccessException {
    	
       	CloseAccountRequest req = (CloseAccountRequest) reqReceived.requestObj;
    	CloseAccountResponse resp = null;
    	
        AccountDetail accountDetail = this.db.query(req.accountNumber);
        if (accountDetail == null) {
        	resp = CloseAccountResponse.failed("This account number doesn't exist");
        } else if (!accountDetail.name.equals(req.name)) {
        	resp = CloseAccountResponse.failed("The account number is not under this name");
        } else if (!accountDetail.password.equals(req.password)) {
        	resp = CloseAccountResponse.failed("Wrong password");
        } else {
            this.db.delete(req.accountNumber);
            this.broadcast(String.format("User %s deletes account with number %d", req.name, req.accountNumber));
            resp = new CloseAccountResponse(true, "");
        }
        
        this.server.sendToClient(new ResponseMessage(reqReceived.id,reqReceived.method,Status.OK, resp));

    }

    
    public void processMonitor(RequestMessage reqReceived) throws IllegalAccessException {
    	
    	MonitorAccountRequest req = (MonitorAccountRequest) reqReceived.requestObj;
        long interval = (long)req.interval;
        this.listeners.put(new InetSocketAddress(req.ipAddress, req.port), Instant.now().plusSeconds(interval));
        System.out.printf("User at %s with port %d starts to monitor for %d seconds\n", req.ipAddress,req.port,interval);
        MonitorAccountStatusResponse resp = new MonitorAccountStatusResponse(true);
        this.server.sendToClient(new ResponseMessage(reqReceived.id,reqReceived.method,Status.OK, resp));
    }
    
    
    private void broadcast(String info) {
        this.purgeListeners();        
        //TODO allow sending to multiple socketAddress.
        this.listeners.forEach((socketAddress, x) -> {
            try {
				this.server.broadcastToRegisteredClients(new ResponseMessage(UUID.randomUUID(),"MonitorAccount",Status.OK, new MonitorAccountResponse(info)), socketAddress);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
    }

    private void purgeListeners() {
        this.listeners.entrySet().removeIf((x) -> {
            return ((Instant)x.getValue()).isBefore(Instant.now());
        });
    }
    
    
}
