package request;

public class QueryAccountRequest {


    public int accountNumber;
    public String password;

    public QueryAccountRequest() {
    }

    public QueryAccountRequest(int accountNumber, String password) {
        this.accountNumber = accountNumber;
        this.password = password;
    }

    public String toString() {
        return "QueryRequest(" + this.accountNumber + ", " + this.password + ")";
    }
}
