package request;

import utils.Currency;

public class OpenAccountRequest {
    public String name;
    public String password;
    public Currency currency;
    public double balance;

    public OpenAccountRequest() {
    }

    public OpenAccountRequest(String name, String password, Currency currency, double balance) {
        this.name = name;
        this.password = password;
        this.currency = currency;
        this.balance = balance;
    }

    public String toString() {
        return "OpenAccountRequest(" + this.name + ", " + this.password + ", " + this.currency + ", " + this.balance + ")";
    }
}
