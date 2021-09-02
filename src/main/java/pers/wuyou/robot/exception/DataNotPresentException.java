package pers.wuyou.robot.exception;

/**
 * @author wuyou
 */
public class DataNotPresentException extends RuntimeException {
    public DataNotPresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
