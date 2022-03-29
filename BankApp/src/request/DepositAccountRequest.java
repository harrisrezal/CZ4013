package request;

import utils.Currency;

public class DepositAccountRequest {
    public String name;
    public int accountNumber;
    public String password;
    public Currency currency;
    public double amount;

    public DepositAccountRequest() {
    }

    public DepositAccountRequest(String name, int accountNumber, String password, Currency currency, double amount) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.password = password;
        this.currency = currency;
        this.amount = amount;
    }

    public String toString() {
        return "DepositRequest(" + this.name + ", " + this.accountNumber + ", " + this.password + ", " + this.currency + ", " + this.amount + ")";
    }
}
