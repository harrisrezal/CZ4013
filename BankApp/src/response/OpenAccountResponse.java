package response;

public class OpenAccountResponse {
    public int accountNumber;

    public OpenAccountResponse() {
    }

    public OpenAccountResponse(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String toString() {
        return "OpenAccountResponse(" + this.accountNumber + ")";
    }
}
