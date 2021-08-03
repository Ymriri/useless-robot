package pers.wuyou.robot.exception;

public class ObjectNotPresentException extends RuntimeException{
    public ObjectNotPresentException() {
    }

    public ObjectNotPresentException(String message) {
        super(message);
    }

    public ObjectNotPresentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectNotPresentException(Throwable cause) {
        super(cause);
    }

    public ObjectNotPresentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
