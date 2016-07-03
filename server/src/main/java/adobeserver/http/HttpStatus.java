package adobeserver.http;


/**
 *  Limited selection of HTTP statuses
 * 
 * 	@author Razvan Manolescu
 */
public enum HttpStatus {
	OK(200, "OK"), NOT_FOUND(404, "Resource not found"), FORBIDDEN(403, "Forbidden"),
	INTERNAL_SERVER_ERROR(500,"Internal Server Error");
	
	
	private int responseCode;
	private String responseReason;
	
	HttpStatus(final int responseCode, final String responseReason) {
		this.responseCode = responseCode;
		this.responseReason = responseReason;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseReason() {
		return responseReason;
	}

	public void setResponseReason(String responseReason) {
		this.responseReason = responseReason;
	}
}