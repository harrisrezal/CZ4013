package response;

import utils.Currency;

public class QueryAccountResponse {
    public String name;
    public Currency currency;
    public double balance;
    public boolean success;
    public String errorMessage;

    public QueryAccountResponse() {
    }

    public QueryAccountResponse(String name, Currency currency, double balance, boolean success, String errorMessage) {
        this.name = name;
        this.currency = currency;
        this.balance = balance;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static QueryAccountResponse failed(String errorMessage) {
        return new QueryAccountResponse("", Currency.USD, 0.0D, false, errorMessage);
    }

    public String toString() {
        return "QueryResponse(" + this.name + ", " + this.currency + ", " + this.balance + ", " + this.success + ", " + this.errorMessage + ")";
    }
}
