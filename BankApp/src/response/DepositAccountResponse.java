package response;

import utils.Currency;

public class DepositAccountResponse {
	
	public String name;
    public double amount;
    public Currency currency;
    public double balance;
    public boolean success;
    public String transaction;
    public String errorMessage;
    public DepositAccountResponse() {
    }

    public DepositAccountResponse(String name, double amount, Currency currency, double balance, boolean success, String transaction, String errorMessage) {
    	this.name = name;
    	this.amount = amount;
        this.currency = currency;
        this.balance = balance;
        this.success = success;
        this.transaction = transaction;
        this.errorMessage = errorMessage;
    }

    public static DepositAccountResponse failed(String errorMessage) {
        return new DepositAccountResponse("",0.0D,Currency.USD, 0.0D, false, "", errorMessage);
    }

    public String toString() {
        return "DepositResponse(" + this.currency + ", " + this.balance + ", " + this.success + ", " + this.errorMessage + ")";
    }

}
