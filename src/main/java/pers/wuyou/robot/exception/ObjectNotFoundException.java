package pers.wuyou.robot.exception;

/**
 * 对象未找到异常
 *
 * @author wuyou<br>
 * 2020年4月29日
 */
public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String message) {
        super(message);
    }

}
