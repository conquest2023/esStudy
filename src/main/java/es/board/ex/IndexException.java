package es.board.ex;

public class IndexException  extends  RuntimeException{

    public IndexException() {
        super();
    }

    public IndexException(Throwable cause) {
        super(cause);
    }

    public IndexException(String message) {
        super(message);
    }

    public IndexException(String message, Throwable cause) {
        super(message, cause);
    }

    protected IndexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
