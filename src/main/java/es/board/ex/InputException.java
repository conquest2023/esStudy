package es.board.ex;

public class InputException extends  RuntimeException{


    public InputException() {
    }

    public InputException(String message) {
        super(message);
    }

    public InputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputException(Throwable cause) {
        super(cause);
    }

    public InputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
