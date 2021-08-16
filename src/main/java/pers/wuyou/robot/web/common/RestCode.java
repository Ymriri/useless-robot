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
    GROUP_CODE_IS_EMPTY(1, "群号为空"),
    KEY_ILLEGAL(2, "key已过期"),
    ;
    public final int code;
    public final String msg;

    RestCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
