package request;

public class CloseAccountRequest {
    public String name;
    public int accountNumber;
    public String password;

    public CloseAccountRequest() {
    }

    public CloseAccountRequest(String name, int accountNumber, String password) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.password = password;
    }

    public String toString() {
        return "CloseAccountRequest(" + this.name + ", " + this.accountNumber + ", " + this.password + ")";
    }
}
