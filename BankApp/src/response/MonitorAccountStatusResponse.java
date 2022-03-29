package response;

public class MonitorAccountStatusResponse {

    public boolean success;

    public MonitorAccountStatusResponse() {
    }

    public MonitorAccountStatusResponse(boolean success) {
        this.success = success;
    }

    public String toString() {
        return "MonitorStatusResponse(" + this.success + ")";
    }

}
