package pers.wuyou.robot.exception;

/**
 * @author wuyou
 */
public class ObjectNotPresentException extends RuntimeException {
    public ObjectNotPresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
