package pers.wuyou.robot.web.common;

import pers.wuyou.robot.exception.WithTypeException;

/**
 * 用户异常
 *
 * @author wuyou
 */
public class UserException extends RuntimeException implements WithTypeException {

    private static final long serialVersionUID = 1L;

    private final Type type;


    public UserException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type type() {
        return type;
    }


    public enum Type {
        /**
         * 用户异常
         */
        USER_NOT_LOGIN, USER_NOT_FOUND, USER_AUTH_FAIL
    }

}
