package app.samflix.macros.user;

public class EmailAlreadyUsedException extends RuntimeException {

	public EmailAlreadyUsedException() {
		super("Email already registered");
	}
}
