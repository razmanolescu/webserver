package adobeserver.server;


/**
 *  Server lifecycle enum
 * 
 * 	@author Razvan Manolescu
 */
public enum LifeCycle {
	STOPPING("Server is attepting to stop"), STARTED("Server started"), RUNNING("Server is running game loop"),
	DRAINING("Server sessions draining"), STOPPED("Server is stopped");
	
	private String extendedMessage;
	
	LifeCycle(final String extendedMessage) {
		this.extendedMessage = extendedMessage;
	}

	public String getExtendedMessage() {
		return extendedMessage;
	}

	public void setExtendedMessage(final String extendedMessage) {
		this.extendedMessage = extendedMessage;
	}

}
