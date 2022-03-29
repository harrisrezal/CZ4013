package response;

public class MonitorAccountResponse {

    public String info;

    public MonitorAccountResponse() {
    }

    public MonitorAccountResponse(String info) {
        this.info = info;
    }

    public String toString() {
        return "MonitorUpdateResponse(" + this.info + ")";
    }

}
