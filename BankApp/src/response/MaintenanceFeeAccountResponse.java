package response;

import utils.Currency;

public class MaintenanceFeeAccountResponse {

    public Currency currency;
    public double balance;
    public boolean success;
    public String errorMessage;

    public MaintenanceFeeAccountResponse() {
    }

    public MaintenanceFeeAccountResponse(Currency currency, Double balance, boolean success, String errorMessage) {
        this.currency = currency;
        this.balance = balance;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static MaintenanceFeeAccountResponse failed(String errorMessage) {
        return new MaintenanceFeeAccountResponse(Currency.USD, 0.0D, false, errorMessage);
    }

    public String toString() {
        return "PayMaintenanceFeeResponse(" + this.balance + ", " + this.success + ", " + this.errorMessage + ")";
    }

}
