package pers.wuyou.robot.web.common;

/**
 * @author wuyou
 */

public enum RestCode {
    /**
     * 返回的状态码和msg
     */
    OK(0, "ok"),
    /**
     * 未知异常(有未处理的异常)
     */
    UNKNOWN_ERROR(1, "未知异常"),
    /**
     * 用户未登录或token已过期
     */
    USER_NOT_LOGIN(2, "用户未登录"),
    KEY_ILLEGAL(2, "key已过期"),
    /**
     * 数据未找到
     */
    DATA_NOT_FOUND(3, "数据不存在"),
    /**
     * 数据已存在
     */
    DATA_ALREADY_EXIST(4, "数据已存在"),
    /**
     * 群号为空
     */
    GROUP_CODE_IS_EMPTY(5, "群号为空"),
    /**
     * 用户验证失败
     */
    USER_VERIFY_FAILED(999, "验证失败,您没有权限"),
    ;
    public final int code;
    public final String msg;

    RestCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
