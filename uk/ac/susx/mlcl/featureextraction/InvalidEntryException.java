package uk.ac.susx.mlcl.featureextraction;

public class InvalidEntryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String msg;
	
	public InvalidEntryException () {
		this("");
	}
	
	public InvalidEntryException (String message) {
		super();
		msg = message;
	}
	
	@Override
	public String getMessage() {
		return msg;
	}

}
