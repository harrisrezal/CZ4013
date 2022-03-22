package response;

public enum Status {
	    NOT_FOUND("service not found"),
	    MALFORMED("malformed request"),
	    INTERNAL_ERR("internal server error"),
	    OK("ok");

	    private final String reason;

	    private Status(String reason) {
	        this.reason = reason;
	    }

	    public String toString() {
	        return this.reason;
	    }
}
