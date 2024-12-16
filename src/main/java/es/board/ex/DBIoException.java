package es.board.ex;

public class DBIoException  extends  RuntimeException{

    public DBIoException() {
        super();
    }

    public DBIoException(Throwable cause) {
        super(cause);
    }

    public DBIoException(String message) {
        super(message);
    }

    public DBIoException(String message, Throwable cause) {
        super(message, cause);
    }

    protected DBIoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
