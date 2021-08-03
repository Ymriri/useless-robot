package pers.wuyou.robot.exception;

public class ObjectCountBeyondException extends RuntimeException {
    public ObjectCountBeyondException() {
    }

    public ObjectCountBeyondException(String message) {
        super(message);
    }

    public ObjectCountBeyondException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectCountBeyondException(Throwable cause) {
        super(cause);
    }

    public ObjectCountBeyondException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
