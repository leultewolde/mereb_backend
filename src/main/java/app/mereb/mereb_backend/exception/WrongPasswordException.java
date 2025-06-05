package app.mereb.mereb_backend.exception;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException() {
        super("Invalid Password");
    }
}
