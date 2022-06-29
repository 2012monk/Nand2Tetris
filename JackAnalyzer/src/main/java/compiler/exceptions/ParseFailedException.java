package compiler.exceptions;

public class ParseFailedException extends RuntimeException {

    public ParseFailedException(Throwable cause) {
        super(cause);
    }

    public ParseFailedException() {
    }

    public ParseFailedException(String message) {
        super(message);
    }
}
