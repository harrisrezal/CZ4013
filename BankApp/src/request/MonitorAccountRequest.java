package request;

public class MonitorAccountRequest {

    public String ipAddress;
    public int port;
    public int interval;

    public MonitorAccountRequest() {
    }

    public MonitorAccountRequest(String ipAddress, int port, int interval) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.interval = interval;
    }

    public String toString() {
        return "MonitorRequest(" + this.interval + ")";
    }

}
