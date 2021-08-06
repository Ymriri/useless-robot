package pers.wuyou.robot.web.common;

/**
 * @author wuyou
 */

public enum RestCode {
    /**
     * 返回的状态码和msg
     */
    OK(0, "ok"),
    @SuppressWarnings("unused")
    ERROR(1, "未知异常"),
    ;
    public final int code;
    public final String msg;

    RestCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
