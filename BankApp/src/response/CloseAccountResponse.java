package response;

public class CloseAccountResponse {

    public boolean success;
    public String errorMessage;

    public CloseAccountResponse() {
    }

    public CloseAccountResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CloseAccountResponse failed(String errorMessage) {
        return new CloseAccountResponse(false, errorMessage);
    }

    public String toString() {
        return "CloseAccountResponse(" + this.success + ", " + this.errorMessage + ")";
    }
}
