package pers.wuyou.robot.exception;

public class MethodAlreadyExistException extends RuntimeException{

    private static final long serialVersionUID = -6494098825180044350L;

    public MethodAlreadyExistException() {
        super();
    }

    public MethodAlreadyExistException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MethodAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodAlreadyExistException(String message) {
        super(message);
    }

    public MethodAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
