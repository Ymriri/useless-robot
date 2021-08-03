package pers.wuyou.robot.exception;

public class FilterDataIllegalException extends RuntimeException{
    public FilterDataIllegalException() {
    }

    public FilterDataIllegalException(String message) {
        super(message);
    }

    public FilterDataIllegalException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilterDataIllegalException(Throwable cause) {
        super(cause);
    }

    public FilterDataIllegalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
