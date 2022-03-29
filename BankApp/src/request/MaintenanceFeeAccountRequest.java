package request;

public class MaintenanceFeeAccountRequest {

    public int accountNumber;
    public String name;
    public String password;

    public MaintenanceFeeAccountRequest() {
    }

    public MaintenanceFeeAccountRequest(int accountNumber, String name, String password) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.password = password;
    }

    public String toString() {
        return "PayMaintenanceFeeRequest(" + this.accountNumber + ", " + this.name + ", " + this.password + ")";
    }

}
