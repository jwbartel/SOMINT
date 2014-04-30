package dropbox.converter;

public class ImapException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ImapException() {
		super();
	}
	
	public ImapException(String message){
		super(message);
	}

}
